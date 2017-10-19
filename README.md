# DATokenSecurity --  云南分布科技
## 简述
Spring Security直接使用时非常反直觉,特别是对于apache shrio用户, 所以构建了这个demo工程, 展示如何最简的方式使用Spring Boot + Spring Security + 类似shorio的角色权限使用方法.
因为本项目尽可能展现基础框架及其原理,并且为了研究方便,所以各部分都使用了最简化实现,只适合demo场景. 在生产环境使用时请根据项目实际情况重写//FIXME部分.

## 环境搭建
---
1. 安装gradle, 去[官网](https://gradle.org/gradle-download/)下载v4.x版本,并安装;
2. 在命令行中执行以下命令生成eclipse和Intellij相关工程文件,并下载相关依赖到本地:
```shell
gradle eclipse idea
```
3. 执行以下命令运行demo应用
```shell
gradle bootRun
```

## 关于我们
+ [官网](https://dataagg.github.io/)
+ [团队博客](https://dataagg.github.io/)
+ [文档](https://watano.gitbooks.io/daframework/content/)
