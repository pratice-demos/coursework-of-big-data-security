import numpy as np
import random
import liu
import kd


# 公共对象
import utils

ope = liu.Liu()
kdtree = None


# 读取 NE 数据集
def get_data():
    float_data = utils.get_ne()

    int_data = float_data * 10 ** 6
    int_data = int_data.astype(int)

    return int_data


# 加密数组，返回密文数组和噪声数组
def enc_data(data):
    length = len(data)
    en_data = np.empty([length, 2], int)
    noise_data = np.empty([length, 2], int)
    for i in range(length):
        en_data[i][0], noise_data[i][0] = ope.enc(data[i][0])
        en_data[i][1], noise_data[i][1] = ope.enc(data[i][1])

    return en_data, noise_data


# 输入查找点
def get_p():
    # 随机生成点
    float_p = np.array([random.random(), random.random()])

    int_p = float_p * 10 ** 6
    int_p = int_p.astype('int64')

    return int_p


# 从数据集中查找点
def get_data_p(data):
    index = random.randrange(0, len(data) - 1)
    return data[index]


# 建立 kd 树，并查找最近邻点
def kd_nearest(en_data, noise, p):
    en_p, noise_p = ope.enc(p)
    en_node, en_dis = kdtree.nearest(en_p)
    index = np.where((en_data[:, 0] == en_node[0]) & (en_data[:, 1] == en_node[1]))[0][0]
    node = ope.dec(en_node, noise[index])

    return node, index


# 暴力搜索最近邻点
def iterate_nearest(data, p):
    dis = np.inf
    node = None
    index = None
    for i in range(len(data)):
        tmp_dis = np.linalg.norm(data[i] - p, 2)
        if dis > tmp_dis:
            dis = tmp_dis
            node = data[i]
            index = i

    return node, index


# 主方法
def main():
    # 读取数据
    data = get_data()
    print('data:\n', data)

    # 对每个元素进行揭序加密
    en_data, noise = enc_data(data)
    print('en_data:\n', en_data)
    print('noise:\n', noise)

    # 构建 kd 树
    global kdtree
    kdtree = kd.KDTree(en_data)

    # 测试
    tests = 100
    cnt = 0
    for i in range(tests):
        # 输入要查找的 p 点
        p = get_p()
        # p = get_data_p(data)
        print('p:\n', p)

        # 在 kd 树上搜索最近邻点
        kd_node, kd_dis = kd_nearest(en_data, noise, p)
        print('kd nearest node:\n', kd_node)

        # 暴力查找最近邻点
        iterate_node, iterate_dis = utils.iterate_nearest(data, p)
        print('iterate nearest node:\n', iterate_node)

        distance = np.linalg.norm(kd_node - iterate_node, 2)
        print('distance:\n', distance)

        if distance == 0:
            cnt += 1

    print('Succeded in: %d out of %d tests.' % (cnt, tests))


if __name__ == '__main__':
    main()