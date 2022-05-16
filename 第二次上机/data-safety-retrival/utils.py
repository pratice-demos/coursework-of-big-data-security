# int 转任意进制字符串
def int_to_str(num, base):
    conv_str = '0123456789ABCDEF'
    if num < base:
        return conv_str[num]
    else:
        return int_to_str(num // base, base) + conv_str[num % base]


# 将十进制字符串转换为二进制字符串
# 按 ascii 编码转换，则每个十进制字符转换为 4 个二进制字符
def dem_to_bin(dem):
    return ''.join([bin(ord(c))[4:] for c in dem])


# 将二进制字符串转换为十进制字符串
def bin_to_dem(bina):
    if len(bina) % 4 != 0:
        return None
    return ''.join([str(int(bina[i*4:(i+1)*4], 2)) for i in range(len(bina) // 4)])
