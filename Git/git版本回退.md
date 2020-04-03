```bash
# 查看历史提交信息
git log

# 本地回退
git reset --soft [id]

#远程回退
git push [remote] [banch] --force
```

源于自己的手贱，git revert 掉了代码，好在自己有commit记录，于是找了一下解决方案：首先`git reflog`查看本地*commit*记录 找到对应的*commit id* 最后 `git reset --hard [commit id]`