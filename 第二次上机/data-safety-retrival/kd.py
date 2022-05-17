import numpy as np


class Node:
    def __init__(self, data, sp=0, left=None, right=None):
        self.data = data
        self.sp = sp  # 0 是按特征 1 排序，1 是按特征 2 排序
        self.left = left
        self.right = right


class KDTree:
    def __init__(self, data):
        self.dim = data.shape[1]  # 维数
        self.root = self.create_tree(data, 0)  # 建树
        self.nearest_node = None  # 最近邻点
        self.nearest_dis = np.inf  # 无穷大

    def create_tree(self, dataset, sp):
        if len(dataset) == 0:
            return None

        # 按特征列进行排序
        dataset_sorted = dataset[np.argsort(dataset[:, sp])]
        # 获取中位数索引
        mid = len(dataset) // 2
        # 生成节点
        left = self.create_tree(dataset_sorted[:mid], (sp + 1) % self.dim)
        right = self.create_tree(dataset_sorted[mid + 1:], (sp + 1) % self.dim)
        parent_node = Node(dataset_sorted[mid], sp, left, right)

        return parent_node

    def nearest(self, x):
        self.nearest_node = None  # 最近邻点
        self.nearest_dis = np.inf  # 无穷大
        def visit(node):
            if node is not None:
                # 分左右或上下
                dis = node.data[node.sp] - x[node.sp]
                # 访问子节点
                visit(node.left if dis >= 0 else node.right)
                # 查看当前子节点到目标节点的距离，二范数求距离
                cur_dis = np.linalg.norm(x - node.data, 2)
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

        return self.nearest_node.data, self.nearest_dis