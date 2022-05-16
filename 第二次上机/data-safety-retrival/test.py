import sys

import numpy as np
import kd
import ore
import random


def test_kd():
    data = np.array([[2, 3], [5, 4], [9, 6], [4, 7], [8, 1], [7, 2]])
    kdtree = kd.KDTree(data)
    node, dis = kdtree.nearest(np.array([2, 3]))
    print(node, dis)


def test_ore():
    enc_cnt = 0
    dec_cnt = 0
    tests = 10
    o = ore.Ore()
    for i in range(tests):
        num1 = random.randrange(2 ** 62, 2 ** 64)
        num2 = random.randrange(2 ** 62, 2 ** 64)

        a = str(num1)
        b = str(num2)
        length = max(len(a), len(b))
        a = '0' * (length - len(a)) + a
        b = '0' * (length - len(b)) + b
        print('a = %s, b = %s' % (a, b))

        c = o.enc(a)
        d = o.enc(b)
        print('c = %s, d = %s' % (c, d))

        ore_num1 = o.cipher_to_num(c)
        ore_num2 = o.cipher_to_num(d)
        print('ore_num1 = %d, ore_num2 = %d' % (ore_num1, ore_num2))

        e = o.dec(c)
        f = o.dec(d)
        print('e = %s, f = %s' % (e, f))

        # 加密保序
        if (ore_num1 - num1) * (ore_num2 - num2) > 0:
            enc_cnt += 1

        # 解密正确
        if a == e and b == f:
            dec_cnt += 1

    print("Succeded in: %d out of %d enc tests." % (enc_cnt, tests))
    print("Succeded in: %d out of %d dec tests." % (dec_cnt, tests))


print('\ntest_kd\n')
test_kd()
print('\ntest_ore_enc\n')
test_ore()
