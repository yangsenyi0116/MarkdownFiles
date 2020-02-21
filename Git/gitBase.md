```bash
# 初始化git
git init

# git 添加要提交的文件
git add *

# git commit
git commit -m “infomations”

# git push
git push remote


# remote
git remote add [url]

git remote remove [url]


# git分支
## 创建新分支
git branch [branchName]

## 创建并切换到新分支
git checkout -b [branchName]

## 切换到新分支
git checkout [branchName]


# 更新远程分支列表
git remote update origin --prune

# 查看所有分支
git branch -a

# 删除远程分支
git push origin --delete [branchName]

# 删除本地分支 
git branch -d  [branchName]


# 合并分支
git  merge [branchName]
```

