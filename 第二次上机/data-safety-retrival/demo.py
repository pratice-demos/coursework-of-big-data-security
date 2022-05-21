from math import sqrt
import random
import copy


class Liu:
    # 初始化密钥
    def __init__(self):
        self.a = random.randrange(10, 100)
        self.b = random.randrange(10, 100)

    # 加密
    def enc(self, plain):
        n = random.randrange(1, self.a - 1)
        cypher = self.a * plain + self.b + n
        return cypher, n

    # 解密
    def dec(self, cypher, n):
        plain = (cypher - n - self.b) // self.a
        return plain


class Node:
    def __init__(self, point, area, left, right):
        self.point = point
        self.area = area
        self.left = left
        self.right = right


class KDtree:
    def __init__(self, array):
        dim = len(array[0])

        def create_kdtree(area, array):
            if len(array) == 0:
                return None
            array.sort(key=lambda x: x[area])  # labda匿名函数 前传的参数 后返回值
            mid = len(array) // 2
            midpoint = array[mid]  # 中间节点
            area_next = (area + 1) % dim
            return Node(midpoint, area,
                        create_kdtree(area_next, array[:mid]), create_kdtree(area_next, array[mid + 1:]))

        self.root = create_kdtree(0, array)

    def nearest(self, x):
        self.nearest_dis = float('inf')
        self.nearest_node = None

        def visit(node):
            if node is not None:
                # 分左右或上下
                dis = node.point[node.area] - x[node.area]
                # 访问子节点
                visit(node.left if dis >= 0 else node.right)
                # 查看当前子节点到目标节点的距离，二范数求距离
                cur_dis = sqrt(sum((p1 - p2) ** 2 for p1, p2 in zip(x, node.point)))
                # 更新节点
                if cur_dis < self.nearest_dis:
                    self.nearest_dis = cur_dis
                    self.nearest_node = node
                # 比较目标节点到当前节点距离是否超过当前超平面，超过了需到另一个子树中
                if self.nearest_dis > abs(dis):
                    visit(node.left if dis < 0 else node.right)

        # 从根节点查找
        root = self.root
        visit(root)

        return self.nearest_node.point, self.nearest_dis


ope = Liu()
kdtree = None


# 从本地文件获取 NE 数据集
def get_ne():
    with open('NE.txt', 'r') as f:
        data = f.readlines()
        # 整数数组
        int_data = []
        for i in range(len(data)):
            nums = data[i].split()
            int_data.append([int(float(item) * 10 ** 6) for item in nums])
    return int_data


# 加密数组，返回密文数组和噪声数组
def enc_data(data):
    length = len(data)
    en_data = []
    noise_data = []
    for i in range(length):
        en1, noise1 = ope.enc(data[i][0])
        en2, noise2 = ope.enc(data[i][1])
        en_data.append([en1, en2])
        noise_data.append([noise1, noise2])

    return en_data, noise_data


# 输入查找点
def get_p():
    # 随机生成点
    int_p = [int(random.random() * 10 ** 6), int(random.random() * 10 ** 6)]

    return int_p


# 建立 kd 树，并查找最近邻点
def nearest(en_data, noise, p):
    en_p = [0, 0]
    noise_p = [0, 0]
    en_p[0], noise_p[0] = ope.enc(p[0])
    en_p[1], noise_p[1] = ope.enc(p[1])
    en_node, en_dis = kdtree.nearest(en_p)
    index = 0
    for i in range(len(en_data)):
        if en_node[0] == en_data[i][0] and en_node[1] == en_data[i][1]:
            index = i
            break
    node = [0, 0]

    node[0] = ope.dec(en_node[0], noise[index][0])
    node[1] = ope.dec(en_node[1], noise[index][1])

    return node, index


# 暴力搜索最近邻点
def iterate_nearest(data, p):
    dis = float('inf')
    index = None
    node = None
    for i in range(len(data)):
        tmp_dis = sqrt(sum((p1 - p2) ** 2 for p1, p2 in zip(data[i], p)))
        if dis > tmp_dis:
            dis = tmp_dis
            node = data[i]
            index = i

    return node, index


# 主方法
def main():
    # 读取数据
    data = get_ne()
    print('data:\n', data[:10])

    # 打印密钥
    print('secret key:', ope.a, ope.b)

    # 对每个元素进行揭序加密
    en_data, noise = enc_data(data)
    print('en_data:\n', en_data[:10])
    print('noise:\n', noise[:10])

    # 构建 kd 树
    global kdtree
    kdtree = KDtree(copy.deepcopy(en_data))

    # 随机生成 p 点
    p = get_p()
    print('p:\n', p)

    # 搜索最近邻
    node, index = nearest(en_data, noise, p)
    print('nearest node:\n', node, index)

    # 暴力查找结果
    i_node, i_index = iterate_nearest(data, p)
    print('iterate nearest node:\n', i_node, i_index)


if __name__ == '__main__':
    main()
