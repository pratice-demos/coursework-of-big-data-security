import random
import numpy as np


class Liu:
    # 初始化密钥
    def __init__(self):
        self.a = random.randrange(1000, 10000)
        self.b = random.randrange(1000, 10000)

    # 加密
    def enc(self, plain):
        n = random.randrange(1, self.b)
        cypher = self.a * plain + self.b + n

        return cypher, n

    # 解密
    def dec(self, cypher, n):
        plain = (cypher - n - self.b) / self.a

        return plain
