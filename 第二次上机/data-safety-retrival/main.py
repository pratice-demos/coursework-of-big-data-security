import numpy as np
import ore
import kd

# 公共对象
o = ore.Ore()
kdtree = None
str_len = 6


# 读取 NE 数据集，返回浮点数二维数组和 10 进制字符串数组
def get_data(path):
    with open(path, 'r') as f:
        data = f.readlines()
        # 浮点数数组
        float_data = np.empty([len(data), 2], dtype=float)
        for i in range(len(data)):
            nums = data[i].split()
            float_data[i] = np.array(list(map(float, nums)))

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


# 输入查找点
def get_p():
    # 输入查找点，0.0 - 0.8
    try:
        ss = input('\n输入查找点，比如 0.059776 0.455814\n')
        ss = np.array(ss.split())
        float_p = ss.astype(float)
    except IndexError:
        print('输出不正确，重新输入')

    int_p = float_p * 10 ** str_len
    int_p = int_p.astype('int64')

    str_p = np.array(list(map(
        lambda item: str(item)[2:2 + str_len] + '0' * (str_len - len(str(item)[2:2 + str_len])), float_p
    )))

    return int_p, str_p


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
    int_data, str_data = get_data('NE.txt')
    print('int_data:\n', int_data)

    # 对每个元素进行揭序加密
    en_int_data = enc_data(str_data)
    print('en_int_data:\n', en_int_data)

    # 构建 kd 树
    global kdtree
    kdtree = kd.KDTree(en_int_data)

    while 1:
        # 输入要查找的 p 点
        int_p, str_p = get_p()
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
        iterate_node, iterate_dis = iterate_nearest(int_data, int_p)
        print('iterate nearest node:\n', iterate_node)

        en_iterate_node, en_iterate_dis = iterate_nearest(en_int_node, en_int_p)
        print('en iterate nearest node:\n', en_iterate_node)


if __name__ == '__main__':
    main()


# TODO ore 加密不能保证明文和密文欧式距离上的偏序关系相同，因此 kd 树不能使用，需改用泰森多边形