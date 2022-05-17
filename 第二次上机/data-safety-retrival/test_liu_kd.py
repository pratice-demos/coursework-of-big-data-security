import numpy as np
import liu
import kd


# 公共对象
ope = liu.Liu()
kdtree = None


# 读取 NE 数据集
def get_data(path):
    with open(path, 'r') as f:
        data = f.readlines()
        # 浮点数数组
        float_data = np.empty([len(data), 2], dtype=float)
        for i in range(len(data)):
            nums = data[i].split()
            float_data[i] = np.array(list(map(float, nums)))

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
    # 输入查找点，0.0 - 0.8
    float_p = None
    try:
        ss = input('\n输入查找点，比如 0.059776 0.455814\n')
        ss = np.array(ss.split())
        float_p = ss.astype(float)
    except IndexError:
        print('输出不正确，重新输入')

    int_p = float_p * 10 ** 6
    int_p = int_p.astype(int)

    return int_p


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
    data = get_data('NE.txt')
    print('data:\n', data)

    # 对每个元素进行揭序加密
    en_data, noise = enc_data(data)
    print('en_data:\n', en_data)
    print('noise:\n', noise)

    # 构建 kd 树
    global kdtree
    kdtree = kd.KDTree(en_data)

    while 1:
        # 输入要查找的 p 点
        p = get_p()
        print('p:\n', p)

        # 在 kd 树上搜索最近邻点
        kd_node, kd_index = kd_nearest(en_data, noise, p)
        print('kd nearest node:\n', kd_node)
        print('kd index:\n', kd_index)

        # 暴力查找最近邻点
        iterate_node, iterate_index = iterate_nearest(data, p)
        print('iterate nearest node:\n', iterate_node)
        print('iterate index:\n', iterate_index)


if __name__ == '__main__':
    main()