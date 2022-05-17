import numpy as np
import kd
import liu
import ore
import random
import utils


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

        c = o.dem_enc(a)
        d = o.dem_enc(b)
        print('c = %s, d = %s' % (c, d))

        ore_num1 = o.cipher_to_num(c)
        ore_num2 = o.cipher_to_num(d)
        print('ore_num1 = %d, ore_num2 = %d' % (ore_num1, ore_num2))

        e = o.dem_dec(c)
        f = o.dem_dec(d)
        print('e = %s, f = %s' % (e, f))

        # 加密保序
        if (ore_num1 - num1) * (ore_num2 - num2) > 0:
            enc_cnt += 1

        # 解密正确
        if a == e and b == f:
            dec_cnt += 1

    print("Succeded in: %d out of %d enc tests." % (enc_cnt, tests))
    print("Succeded in: %d out of %d dec tests." % (dec_cnt, tests))


def test_liu():
    enc_cnt = 0
    dec_cnt = 0
    tests = 10
    l = liu.Liu()
    for i in range(tests):
        num1 = random.randrange(2 ** 62, 2 ** 64)
        num2 = random.randrange(2 ** 62, 2 ** 64)
        print('num1 = %d, num2 = %d' % (num1, num2))

        a, n1 = l.enc(num1)
        b, n2 = l.enc(num2)
        print('a = %d, b = %d' % (a, b))

        c = l.dec(a, n1)
        d = l.dec(b, n2)
        print('c = %d, d = %d' % (c, d))

        # 加密保序
        if (num1 - a) * (num2 - b) > 0:
            enc_cnt += 1

        # 解密正确
        if num1 == c and num2 == d:
            dec_cnt += 1

    print("Succeded in: %d out of %d enc tests." % (enc_cnt, tests))
    print("Succeded in: %d out of %d dec tests." % (dec_cnt, tests))


def test_kd():
    # 生成 data
    length = 1000
    data = np.empty([length, 2], 'int64')
    for i in range(length):
        data[i][0] = random.randrange(1, 10 ** 5)
        data[i][1] = random.randrange(1, 10 ** 5)

    print(data)

    # 建立 kd 树
    kdtree = kd.KDTree(data)

    # 测试
    tests = 100
    cnt = 0
    for i in range(tests):
        p = np.array([random.randrange(1, 10 ** 5), random.randrange(1, 10 ** 5)])
        # kd 查找最近点
        kd_node, kd_dis = kdtree.nearest(p)
        print('kd_node & kd_dis\n', kd_node, kd_dis)
        # 暴力查找
        iterate_node, iterate_dis = utils.iterate_nearest(data, p)
        print('iterate_node & iterate_dis\n', iterate_node, iterate_dis)
        # 计算距离
        distance = np.linalg.norm(kd_node - iterate_node, 2)
        print('distance:\n', distance)

        if distance == 0:
            cnt += 1

    print("Succeded in: %d out of %d tests." % (cnt, tests))


# print('\ntest_ore_enc\n')
# test_ore()
# print('\ntest_ore_enc\n')
# test_liu()
print('\ntest_kd\n')
test_kd()
