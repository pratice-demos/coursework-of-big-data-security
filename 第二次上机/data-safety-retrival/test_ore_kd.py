import numpy as np
import random
import ore
import kd
import utils


o = ore.Ore()
kdtree = None
str_len = 6


# 读取 NE 数据集，返回浮点数二维数组和 10 进制字符串数组
def get_data():
    float_data = utils.get_ne()

    int_data = float_data * 10 ** str_len
    int_data = int_data.astype('int64')

    # 转换为 10 进制字符串，字符串长度为 6
    str_data = np.array(list(map(
        lambda arr: list(map(
            lambda item: str(item)[2:2 + str_len] + '0' * (str_len - len(str(item)[2:2 + str_len])), arr
        )),
        float_data
    )))

    return int_data, str_data


# 加密 10 进制字符串数组，返回整数数组
def enc_data(str_data):
    length = len(str_data)
    en_int_data = np.empty([length, 2], 'int64')
    for i in range(length):
        en_int_data[i][0] = o.cipher_to_num(o.dem_enc(str_data[i][0]))
        en_int_data[i][1] = o.cipher_to_num(o.dem_enc(str_data[i][1]))

    return en_int_data


# 随机生成查找点，范围位于 0.01 - 1.00
def get_p():
    # 随机生成点
    float_p = np.array([random.random(), random.random()])

    int_p = float_p * 10 ** str_len
    int_p = int_p.astype('int64')

    str_p = np.array(list(map(
        lambda item: str(item)[2:2 + str_len] + '0' * (str_len - len(str(item)[2:2 + str_len])), float_p
    )))

    return int_p, str_p


# 从数据集中随机返回明文点
def get_data_p(int_data, str_data):
    index = random.randrange(0, len(int_data) - 1)
    return int_data[index], str_data[index]


# 将明文点加密
def enc_p(str_p):
    en_str_p = np.array([o.dem_enc(str_p[0]), o.dem_enc(str_p[1])])

    en_int_p = np.array(list(map(
        lambda item: o.cipher_to_num(item),
        en_str_p
    )))

    return en_str_p, en_int_p


# 将密文点解密
def dec_p(en_str_p):
    str_p = np.array(list(map(
        lambda item: o.dem_dec(item),
        en_str_p
    )))

    int_p = str_p.astype('int64')

    return int_p, str_p


# 建立 kd 树，并查找最近邻点
def kd_nearest(en_int_p):
    en_int_node, en_int_dis = kdtree.nearest(en_int_p)

    en_str_node = np.array(list(map(
        lambda item: o.num_to_cipher(item, str_len * o.BINLEN),
        en_int_node
    )))

    return en_int_node, en_str_node


# 暴力搜索最近邻点
def iterate_nearest(int_data, int_p):
    dis = np.inf
    node = None
    for item in int_data:
        tmp_dis = np.linalg.norm(item - int_p, 2)
        if dis > tmp_dis:
            dis = tmp_dis
            node = item

    return node, dis


# 主方法
def main():
    # 读取数据
    int_data, str_data = get_data()
    print('int_data:\n', int_data)

    # 对每个元素进行揭序加密
    en_int_data = enc_data(str_data)
    print('en_int_data:\n', en_int_data)

    # 构建 kd 树
    global kdtree
    kdtree = kd.KDTree(en_int_data)

    # 测试
    tests = 100
    cnt = 0
    for i in range(tests):
        # 查找的 p 点
        int_p, str_p = get_p()
        # int_p, str_p = get_data_p(int_data, str_data)
        print('int_p:\n', int_p)

        # 加密 p 点
        en_str_p, en_int_p = enc_p(str_p)
        print('en_int_p:\n', en_int_p)

        # 在 kd 树上搜索最近邻点
        en_int_node, en_str_node = kd_nearest(en_int_p)
        print('en_int_node:\n', en_int_node)

        # 解密
        int_node, str_node = dec_p(en_str_node)
        print('kd nearest node:\n', int_node)

        # 暴力查找最近邻点
        iterate_node, iterate_dis = utils.iterate_nearest(int_data, int_p)
        print('iterate nearest node:\n', iterate_node)

        distance = np.linalg.norm(int_node - iterate_node, 2)
        print('distance:\n', distance)

        if distance == 0:
            cnt += 1

    print('Succeded in: %d out of %d tests.' % (cnt, tests))


if __name__ == '__main__':
    main()
