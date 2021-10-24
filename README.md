# PigGame 猪尾巴游戏

<a href="https://github.com/Bngel">
  <img src="https://badgen.net/badge/Author/bngel/blue?icon=telegram"/>
</a>
<a href="https://github.com/Bngel/PigGame">
  <img src="https://badgen.net/badge/PigGame/public/black?icon=github"/>
</a>
<a>
  <img src="https://badgen.net/badge/Language/kotlin/pink?icon=eclipse"/>
</a>

## 导读

- [背景](#背景)
- [安装](#安装)
- [使用说明](#使用说明)
- [维护者](#维护者)
- [如何贡献](#如何贡献)
- [使用许可](#使用许可)

## 背景

这个项目是2019级FZU的软工实践的一次结对编程项目.</br>
由 bngel 与 mcj 进行组队, mcj 负责产品原型的设定, bngel 负责将其进行实现.</br>
项目选择使用安卓作为游戏的表现形式, 完全使用`kotlin`作为开发语言.</br>

## 安装

当前最新的`apk`版本为`1.1.1-RELEASE`, 已经发布在`release`中</br>
<a href="https://github.com/Bngel/PigGame/releases/tag/1.1.0-RELEASE">release</a></br>
欢迎各位进行下载使用. (联机需要连接校园网)</br>

## 使用说明

### 1. 联机对战

- 需要连接校园网才能够访问`API`, 因此联机对战全程都必须连接校园网使用
- `1.0.0-SNAPSHOT`版本的托管仅支持自动翻牌, 无任何策略属性
- 在新增的`1.0.0-GA`的稳定版本中, 新增了关于机器人AI的逻辑, 能够进行出牌与翻牌的处理

### 2. 双人对战

- 双人对战的形式为同一台手机进行双方视角的切换, 即当前玩家执行操作后切换到对方回合时作为对手出现.
- 最后的结算结果以当前玩家视角作为参照. 即 当前/对立 玩家胜利.

### 3. 人机对战
- 与联机对战相同, `1.0.0-SNAPSHOT`版本的人机只支持翻牌操作. 因此, 很笨.
- `1.0.0-GA`版本的人机新增了AI逻辑, 使得更加难以战胜. (弱欸

## 维护者

> bngel

## 如何贡献

- 可以通过提`issue`的方式来帮助改进
- 也可以直接`pull request`直接成为一名`contributor`
- 直接QQ私信我 (划掉

## 使用许可

-------

    Copyright 2013 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

