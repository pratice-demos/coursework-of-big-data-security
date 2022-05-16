import hashlib
import random
from string import ascii_uppercase, digits
import utils


class Ore:
    LEN = 16    # 加密长度
    KLEN = 10   # 密钥长度
    BASE = 3    # 密文以 BASE 进制的字符串存储
    BINLEN = 4  # 每个十进制字符转换为 4 个二进制字符

    def __init__(self):
        self.key = ''
        self.rnd_key()

    # 生成 n 位密钥字符串
    def rnd_key(self):
        self.key = ''.join(random.choice(ascii_uppercase + digits) for _ in range(self.KLEN))

    # 对前 i 位明文加密，返回整数
    def prf(self, msg):
        pad = '0' * (self.LEN - len(msg))
        return int(hashlib.sha224((str(msg) + pad + str(self.key)).encode('utf-8')).hexdigest(), 16)

    # 输入 2 进制明文字符串，返回 3 进制密文字符串
    def bin_enc(self, m):
        tmp_m = ''          # 存储已处理的明文
        tmp_res = ''        # 存储已生成的密文
        for i in m:
            tmp_m += i
            tmp_res += str((self.prf(tmp_m[:-1]) + int(tmp_m[-1])) % self.BASE)
        return tmp_res

    # 输入 3 进制密文字符串，返回 2 进制明文字符串
    def bin_dec(self, m):
        tmp_res = ''    # 存储已得到的明文
        for i in m:
            # 如果当前位为 0，则加密当前位得到的密文应该和密文当前位相等
            # 只需对 0 和 1 进行猜测就可得到正确结果
            if (self.prf(tmp_res) + 0) % self.BASE == int(i, self.BASE):
                tmp_res += '0'
            else:
                tmp_res += '1'
        return tmp_res

    # 对 10 进制明文字符串进行加密，返回 3 进制密文字符串
    def dem_enc(self, m):
        # 将 10 进制明文字符串转换为 2 进制字符串
        bin_m = utils.dem_to_bin(m)
        return self.bin_enc(bin_m)

    # 对 3 进制密文字符串解密，返回 10 进制明文字符串
    def dem_dec(self, m):
        res = self.bin_dec(m)
        # 将 2 进制字符串转换为 10 进制字符串
        data = utils.bin_to_dem(res)
        return data

    # 将 3 进制密文字符串转换为 10 进制密文整数，便于比较大小和计算距离
    def cipher_to_num(self, cipher):
        return int(cipher, self.BASE)

    # 将 10 进制密文整数转换为 3 进制密文字符串
    def num_to_cipher(self, num, length):
        cipher = utils.int_to_str(num, self.BASE)
        cipher = '0' * (length - len(cipher)) + cipher
        return cipher
