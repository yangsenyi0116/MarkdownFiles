## rebase 变基

### rebase的定义



```csharp
git-rebase - Reapply commits on top of another base tip
```

即将当前分支的提交放在其他分支的最新的提交后。但是这个`reapply`并非简单地将提交剪切复制到，**Rebase 实际上就是取出一系列的提交记录，“复制”它们，然后在另外一个地方逐个的放下去。**

### rebase详解

在 Git 中整合来自不同分支的修改主要有两种方法：`merge` 以及 `rebase`。 在本节中我们将学习什么是“变基”，怎样使用“变基”，并将展示该操作的惊艳之处，以及指出在何种情况下你应避免使用它。

`git rebase`用于把一个分支的修改合并到当前分支。
 假设你现在基于远程分支"origin"，创建一个叫"mywork"的分支。



```ruby
$ git checkout -b mywork origin
```

但是与此同时，有些人也在"origin"分支上做了一些修改并且做了提交了. 这就意味着"origin"和"mywork"这两个分支各自"前进"了，它们之间"分叉"了。

![img](https:////upload-images.jianshu.io/upload_images/225323-ad23095420f64e2e.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/421/format/webp)

image

如果你想让"mywork"分支历史看起来像没有经过任何合并一样，你也许可以用`git rebase`:



```ruby
$ git checkout mywork
$ git rebase origin
```

这些命令会把你的"mywork"分支里的每个提交(commit)取消掉，并且把它们临时保存为补丁(patch)(这些补丁放到".git/rebase"目录中),然后把"mywork"分支更新 为最新的"origin"分支，最后把保存的这些补丁应用到"mywork"分支上。

你也可以看到以前的提交并没有被销毁。他们根本无法直接访问。如果你还记得，分支只是一个指向提交的指针。因此，如果分支和标签都不指向提交，则这些提交几乎不可能被访问，但这些提交仍然存在。

![img](https:////upload-images.jianshu.io/upload_images/225323-d1a9b8755950e4aa.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/652/format/webp)

image

当'mywork'分支更新之后，它会指向这些新创建的提交(commit),而那些老的提交会被丢弃。 如果运行垃圾收集命令(pruning garbage collection), 这些被丢弃的提交就会删除. （请查看 git gc)

以前遇到commit写错总是使用git reset --soft回退到之前的状态，再commit后push -f强推到远程库，能够覆盖掉之前的commit。

现在想想也是很low的做法，git rebase 可以帮你搞定这个问题。

好了，随便提交了几个



![img](https:////upload-images.jianshu.io/upload_images/225323-dda75076b72d8c5c.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/700/format/webp)

image

在使用git时，我们通常非常频繁地向repo中做commit，但是我们的commit本身往往是零散的不连续的，比如：

我在不同的topic之间来回切换，这样会导致我的历史中不同topic互相交叉，逻辑上组织混乱；
 我们可能需要多个连续的commit来解决一个bug；
 我可能会在commit中写了错别字，后来又做修改；
 甚至我们在一次提交时纯粹就是因为懒惰的原因，我可能吧很多的变更都放在一个commit中做了提交。
 上面的各种行为只要是保留在local repo中，这是没有问题的，也是正常的，但是如果为了尊重别人同时也为了自己将来能够返回来我绝对避免将这些杂乱的历史信息push到remote上去。在我push之前，我会使用git rebase -i的命令来清理一下历史。

### rebase的基本操作

回顾之前在 分支的合并 中的一个例子，会看到开发任务分叉到两个不同分支，又各自提交了更新。

![img](https:////upload-images.jianshu.io/upload_images/225323-ed1ea6f2a9f130c5.png?imageMogr2/auto-orient/strip|imageView2/2/w/800/format/webp)

image

整合分支最容易的方法是 merge 命令。 它会把两个分支的最新快照（C3 和 C4）以及二者最近的共同祖先（C2）进行三方合并，合并的结果是生成一个新的快照（并提交）。

![img](https:////upload-images.jianshu.io/upload_images/225323-308399452a3f3687.png?imageMogr2/auto-orient/strip|imageView2/2/w/800/format/webp)

image

其实，还有一种方法：你可以提取在 C4 中引入的补丁和修改，然后在 C3 的基础上应用一次。 在 Git 中，这种操作就叫做 变基。 你可以使用 rebase 命令将提交到某一分支上的所有修改都移至另一分支上，就好像“重新播放”一样。

![img](https:////upload-images.jianshu.io/upload_images/225323-a5cfcd5da17c084e.png?imageMogr2/auto-orient/strip|imageView2/2/w/800/format/webp)

image

它的原理是首先找到这两个分支（即当前分支 experiment、变基操作的目标基底分支 master）的最近共同祖先 C2，然后对比当前分支相对于该祖先的历次提交，提取相应的修改并存为临时文件，然后将当前分支指向目标基底 C3, 最后以此将之前另存为临时文件的修改依序应用。（译注：写明了 commit id，以便理解，下同）

此时，C4' 指向的快照就和上面使用 merge 命令的例子中 C5 指向的快照一模一样了。 这两种整合方法的最终结果没有任何区别，但是变基使得提交历史更加整洁。 你在查看一个经过变基的分支的历史记录时会发现，尽管实际的开发工作是并行的，但它们看上去就像是串行的一样，提交历史是一条直线没有分叉。

一般我们这样做的目的是为了确保在向远程分支推送时能保持提交历史的整洁——例如向某个其他人维护的项目贡献代码时。 在这种情况下，你首先在自己的分支里进行开发，当开发完成时你需要先将你的代码变基到 origin/master 上，然后再向主项目提交修改。 这样的话，该项目的维护者就不再需要进行整合工作，只需要快进合并便可。

### 举例

两个分支`master`和`bugfix`都提交了一些commit,使用rebase变基。

1. 切换到`bugfix`然后将`bugfix`的提交变基到`master`



```console
$ git checkout bugfix
$ git rebase master
```

![img](https:////upload-images.jianshu.io/upload_images/225323-6d88d00355505daf.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/836/format/webp)

image



现在 bugFix 分支上的工作在 master 的最顶端，同时我们也得到了一个更线性的提交序列。

![img](https:////upload-images.jianshu.io/upload_images/225323-4b2cd45ce6641177.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/852/format/webp)

image

注意，提交记录 C3 依然存在（树上那个半透明的节点），而 C3' 是我们 Rebase 到 master 分支上的 C3 的副本。

现在`master`还没有更新，下面咱们就来更新它吧

1. 切换到`master`上，然后rebase变基`bugfix`分支



```console
$ git checkout master
$ git rebase bugfix
```

![img](https:////upload-images.jianshu.io/upload_images/225323-a3aec8f8d80fd54f.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/820/format/webp)

image

好了！由于 `bugFix` 继承自 `master`，所以 Git 只是简单的把 `master` 分支的引用向前移动了一下而已。

![img](https:////upload-images.jianshu.io/upload_images/225323-8ba6c6ab20d5e455.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/870/format/webp)

image

### rebase 和 merge

无论是通过rebase，还是通过merge合并，整合的最终结果所指向的快照始终是一样的，只不过提交历史不同罢了。

- **rebase是将一系列提交按照原有次序依次应用到另一分支上**
- **merge是把最终结果合在一起。**

### 二、解决冲突

在rebase的过程中，也许会出现冲突(conflict). 在这种情况，Git会停止rebase并会让你去解决 冲突；在解决完冲突后，用"git-add"命令去更新这些内容的索引(index), 然后，你无需执行 git-commit,只要执行:



```kotlin
$ git rebase --continue
```

这样git会继续应用(apply)余下的补丁。
 在任何时候，你可以用--abort参数来终止rebase的行动，并且"mywork" 分支会回到rebase开始前的状态。



```cpp
$ git rebase --abort
```

#### `git rebase -i`

工作中，我们可能不小心写错commit，例如上面那个 “测试 git rebase”我写错了，我想改一改，怎么办呢？

1.（打开terminal）



```console
git rebase -i 233d7b3( 这个commit是在我们要修改的commit前一个)

git rebase -i 233d7b3
```

然后就进入下面这里：

![img](https:////upload-images.jianshu.io/upload_images/225323-56574b77d019a9b4.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/700/format/webp)

image

这里就是我们熟悉的vi，按i进入insert模式，

我们是要修改，所以改成



```console
reword 345c70f 测试 git rebase

     esc ： wq 保存退出
```

![img](https:////upload-images.jianshu.io/upload_images/225323-fc73afb77a150f73.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/700/format/webp)

image

i进入insert模式，修改commit内容，esc  ： wq 保存退出。

最后还需要强push

git push --force

那么我们要如何合并几个commit呢？

和上面类似，我们首先



```console
git rebase -i f290515(我们要合并的commit的前一个)
```

![img](https:////upload-images.jianshu.io/upload_images/225323-e7f2242b895237ac.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/700/format/webp)

image



```console
pick 766f348 dsfdsf

squash 233d7b3 sdfdsf

squash 345c70f 测试 git rebase
```

我们可以这样修改  将后面两个改成`squash`，就是合并到第一个上去

如果没有冲突就可以看到这个界面



![img](https:////upload-images.jianshu.io/upload_images/225323-02b77ddafae957c2.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/580/format/webp)

image

保存退出

最后`git push -f`

#### rebase和merge有什么区别呢

![img](https:////upload-images.jianshu.io/upload_images/225323-a6b08d38a5c7da2b.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/193/format/webp)

image

现在我们在这个分支做一些修改，然后生成两个提交(commit).



```console
$ 修改文件
$ git commit
$ 修改文件
$ git commit
```

但是与此同时，有些人也在"origin"分支上做了一些修改并且做了提交了. 这就意味着"origin"和"mywork"这两个分支各自"前进"了，它们之间"分叉"了

![img](https:////upload-images.jianshu.io/upload_images/225323-df5eee9b77763b80.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/421/format/webp)

image

在这里，你可以用"pull"命令把"origin"分支上的修改拉下来并且和你的修改合并； 结果看起来就像一个新的"合并的提交"(merge commit):



![img](https:////upload-images.jianshu.io/upload_images/225323-d6e13ce929bfa939.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/551/format/webp)

image



但是，如果你想让"mywork"分支历史看起来像没有经过任何合并一样，你也许可以用 git rebase:



```console
$ git checkout mywork
$ git rebase origin
```

这些命令会把你的"mywork"分支里的每个提交(commit)取消掉，并且把它们临时 保存为补丁(patch)(这些补丁放到".git/rebase"目录中),然后把"mywork"分支更新 为最新的"origin"分支，最后把保存的这些补丁应用到"mywork"分支上。

![img](https:////upload-images.jianshu.io/upload_images/225323-898a55ab0a83b100.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/652/format/webp)

image



![img](https:////upload-images.jianshu.io/upload_images/225323-bc6fc0b4d3aad957.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/700/format/webp)

image

### 快速sum up： 核心工作流原则和心法

下面的几个心法是你在使用git时必须磨砺在心的，在本文的后面，我们将具体说明哪些命令来负责执行这些心法：

1. 当我需要merge一个临时的本地branch时。。。我确保这个branch不会在版本变更历史图谱中显示，我总是使用一个fast-forward merge策略来merge这类branch，而这往往需要在merge之前做一个rebase;
2. 当我需要merge一个项目组都知道的local branch时。。。我得确保这个branch的信息会在历史图谱中一直展示，我总是执行一个true merge;
3. 当我准备push我的本地工作时。。。我得首先清理我的本地历史信息以便我总是push一些清晰易读有用的功能；
4. 当我的push由于和别人已经发布的工作相冲突而被拒绝时，我总是rebase更新到最新的remote branch以避免用一些无意义的micro-merge来污染历史图谱

#### 聪明地merge一个branch

前面讲过，你只有在需要合并融入一个分支所提供的所有feature时才做merge。在这时，你需要问你的核心的问题是：这个分支需要在历史图谱中展示吗？

当这个分支代表了一个团队都熟知的一块工作时（比如在项目管理系统中的一个task,一个关联到一个ticket的bugfix,一个user story或者use case的实现，一个项目文档工作等），那么在这种情况下，我们应该将branch的信息永远留存在产品历史图谱中，甚至即使branch本身已经被删除。

否则，如果不代表一个well-known body of work,那么branch本身仅仅是一个技术意义上的实体，我们没有理由将它呈现在产品历史图谱中。我们得使用一个rebase+fast-forward merge来完成merge。

我们来看看上面两种场景分别长什么样：

通过"true merge"来保留历史信息
 我们假设我们一个乘坐oauth-signin的feature branch,该branch的merge 目标是master.

如果master分支在oauth-signin分支从master创建后又往前走了一些commits（这可能是由于其他的branch已经merge到了master,或者在master上直接做了commit，或者有人在master上cherry-picked了一些commits)，那么这时在master和oauth-signin之间就产生了分叉（也就是说master不可能在不会退的情况下直接到oauth-signin)。在这种情况下，git将会自动地产生一个"true merge"

这是我们要的也是我们希望的，并不需要任何额外工作。

然而，如果master在oauth-signin创建后并未向前走，后者就是master的直接后代（无分叉），这时GIT默认地在merge时是执行一个fast-forward的merge策略，git并不会创建一个merge commit而是简单地把master分支标签移动到oauth-signin分支tip所指向的commit。这时oauth-sigin分支就变成了一个"透明"的分支了：在历史图谱中无法得知oauth-signin分支的起始位置在哪里，并且一旦这个branch被删除，那么从历史上我们再也无法看到任何关于这个开发分支曾经存在的历史渊源。

这不是我们所想要的，所以我们通过强制git产生一个真正的merge---通过使用--no-ff参数（no fast forward的意思）。

通过fast-forward merge来实现merge的透明
 这是相反的情况：我们的branch因为没有任何实质语义，所以我们不希望它在历史图谱中存在。我们必须确保merge会使用fast-forward策略。

我们假设我们有一个仅仅为了开发的安全性起了一个local branch命名为quick-fixes,而master仍然是要merge到的目标分支。

如果master在quick-fixes创建之后再也没有往前走，我们知道git会产生一个fast-forward的merge:

另一方面，如果master在quick-fixes创建后又往前走了的话，我们如果直接merge的话git会给我们一个true merge，产生一个merge commit，那么我们的branch就会污染历史图谱，这不是我们想要的。

在这种情况下，我们要做的事调整quick-fixes分支使得它重新成为master分支的直接后代（也就是不再分叉），这样就可以fast-forward merge了。要完成这个目的，我们需要使用git rebase命令。我们希望通过更改quick-fixes分支的base commit，以便它的base commit不再是master的老tip，而是当前的tip(注意tip是随着commit的不断引入而不断往前移动的！）。这个动作会重写quick-fixes分支的历史，由于quick-fixes完全是本地分支，重写历史是无关紧要的。

在这里我们特别要注意这个场景是如何运作的：

1. 我们有一个分叉过的分支但是我们希望透明化地merge，所以。。。
2. 我们首先变基到master的最新commit;
3. 我们随后到master上，执行merge命令就产生一个fast-forward

注意：我这里额外提醒一下，实际上我们看到上面的word1,word2,word3的commit可能还是不爽，我们在第3.步骤中可以使用git merge quick-fixes --squash，来讲所有的word1,2,3都合并成一个commit;

注意留心merge的一些默认配置
 如果在练习上面的操作时，你发现git并未如你所愿，你需要检查一下git对于merge的一些默认配置。

比如:branch.master.mergeoptions = --no-ff/merge.ff=false或者branch.master.mergeoptions=--ff-only/merge.ff=only

#### Rebase一个古老的分支

有时候你创建一个feature分支开始工作后可能很长时间没有时间再做这个feature开发，当你回来时，你的feature分支就会缺失很多master上的bugfix或者一些其他的feature。在这种个情况下，我们先假设除了你没有其他人在这个分支上工作，那么你可以rebase你的feature分支：

git rebase [basebranch] [topicbranch] 注意这时git rebase的参数顺序，第一个为基分支，第二个为要变基的分支

(master) $ git rebase master better-stats
 注意：如果那个feature分支已经被push到remote了的话，你必须使用-f参数来push它，以便你覆盖这个分支的commits历史，这时覆盖这个branch历史也无所谓，因为历史的所有commits都已经相应重新生成了！！。（一个分支的历史由分支的起始commit和头tip commit来描述.有一点需要注意：一旦我们做一次rebase后，那么这个分支上的所有commit由于这次变基，其commit HASH都会改变！！）另外需要注意我们只能对private分支做这个rebase并且git push --force操作！！

#### 在Push之前清理你的本地历史

如果你正确地使用git，相信我们都会频繁地做一些原子commit.我们也要铭记以下警句：不要落入SVN人员的行为模式：commit+push，这是集中式版本控制系统的最常见工作模式：每一个commit都立即push到server上。

事实上，如果那样做的话，你就失去了分布式版本控制系统的灵活性：只要我们没有push，我们就有足够的灵活性。所有我们本地的commits只要没有push都是我们自己的，所以我们有完全的自由来清理这些commit，甚至删除取消某些commits。为什么我们要那么每个commit都频繁地Push从而失去我们应该有的灵活性呢？

在一个git的典型工作流中，你每天可能会产生10到30个commit,但是我们往往可能只会push 2到3次，甚至更少。

再次重申：在push之前，我应该清理我的本地历史。

有很多原因会导致我们的本地历史是混乱的，前面已经提及，但是现在还想再说一遍：

我在不同的topic之间来回切换，这样会导致我的历史中不同topic互相交叉，逻辑上组织混乱；
 我们可能需要多个连续的commit来解决一个bug；
 我可能会在commit中写了错别字，后来又做修改；
 甚至我们在一次提交时纯粹就是因为懒惰的原因，我可能吧很多的变更都放在一个commit中做了提交。
 这些场景都会导致一个混乱的历史产生，非常难以阅读，难以理解，难以被他人所重用，注意：这里的他人也可能是你自己哦，想想两个月后你再来看这段代码吧。

幸运的是，git给你提供了一个漂亮的方法来不用花什么精力就能理清你的本地历史：

1. reorder commits;
2. squash them together;
3. split one up(trickier)
4. remove commits altogether;
5. rephrase commit messages

interactive rebasing就和普通的rebase很相像，它给你一个个地选择编辑你的commit的机会。

在我们当下rebase -i的情形，rebase操作本身并不会实际的真真实实地变基。rebase -i操作仅仅会重写历史。在每天的工作场景中，可能那个分支已经在你的远端库中存在（也就是说已经发布了），你需要做的是清理自从最近一次git pull之后的所有local commits。假设你正在一个experiment分支。你的命令行可能是这样的：



```console
(experiment) $ git rebase -i origin/experiment
```

在这里你在rebase你的当前分支（experiment分支)到一个已经存在历史中的commit(origin/experiment).如果这个rebase不是interactive的话，那么这个动作是毫无意义的（实际上会被作为一个短路的no-op).但是正是有了这个-i选项，你将可以编辑rebase将要执行的这个脚本化过程。那个脚本将会打开一个git editor,就像我们在commit提交时弹出的编辑框一样。

## rebase的切换

在对两个分支进行变基时，所生成的“重放”并不一定要在目标分支上应用，你也可以指定另外的一个分支进行应用。 就像 从一个特性分支里再分出一个特性分支的提交历史 中的例子那样。 你创建了一个特性分支 server，为服务端添加了一些功能，提交了 C3 和 C4。 然后从 C3 上创建了特性分支 client，为客户端添加了一些功能，提交了 C8 和 C9。 最后，你回到 server 分支，又提交了 C10。

![img](https:////upload-images.jianshu.io/upload_images/225323-824272cee434bb87.png?imageMogr2/auto-orient/strip|imageView2/2/w/800/format/webp)

image

假设你希望将 client 中的修改合并到主分支并发布，但暂时并不想合并 server 中的修改，因为它们还需要经过更全面的测试。 这时，你就可以使用 git rebase 命令的 --onto 选项，选中在 client 分支里但不在 server 分支里的修改（即 C8 和 C9），将它们在 master 分支上重放：



```console
$ git rebase --onto master server client
```

以上命令的意思是：“取出 client 分支，找出处于 client 分支和 server 分支的共同祖先之后的修改，然后把它们在 master 分支上重放一遍”。 这理解起来有一点复杂，不过效果非常酷。

现在可以快进合并 master 分支了。（如图 快进合并 master 分支，使之包含来自 client 分支的修改）：



```console
$ git checkout master
$ git merge client
```

![img](https:////upload-images.jianshu.io/upload_images/225323-facdc094b53a23a3.png?imageMogr2/auto-orient/strip|imageView2/2/w/800/format/webp)

image

接下来你决定将 server 分支中的修改也整合进来。 使用 git rebase [basebranch] [topicbranch] 命令可以直接将特性分支（即本例中的 server）变基到目标分支（即 master）上。这样做能省去你先切换到 server 分支，再对其执行变基命令的多个步骤。



```ruby
$ git rebase master server
```

如图 将 server 中的修改变基到 master 上 所示，server 中的代码被“续”到了 master 后面。



![img](https:////upload-images.jianshu.io/upload_images/225323-27503026e0dab82d.png?imageMogr2/auto-orient/strip|imageView2/2/w/800/format/webp)

image

## rebase的风险

### rebase黄金定律

**不要对在你的仓库外有副本的分支（共享分支）执行变基。**

- 共享分支，我的意思是存在于远端存储库中的分支以及您团队中的其他人员可以进行分支。

*rebase操作的实质是丢弃一些现有的提交，然后相应地新建一些内容一样但实际上不同的提交*。 如果你已经将提交推送至某个仓库，而其他人也已经从该仓库拉取提交并进行了后续工作，此时，如果你用 `git rebase` 命令重新整理了提交并再次推送，你的同伴因此将不得不再次将他们手头的工作与你的提交进行整合，如果接下来你还要拉取并整合他们修改过的提交，事情就会变得一团糟。

永远不要rebase一个已经分享的分支（到非remote分支，比如rebase到master,develop,release分支上），也就是说永远不要rebase一个已经在中央库中存在的分支.只能rebase你自己使用的私有分支

如你和你的同事John都工作在一个feature开发上，你和他分别做了一些commit，随后你fetch了John的feature分支（或者已经被John分享到中央库的feature分支），那么你的repo的版本历史可能已经是下面的样子了：



![img](https:////upload-images.jianshu.io/upload_images/225323-b3bef942ce77d1c9.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/374/format/webp)

image

这时你希望集成John的feature开发工作，你也有两个选择，要么merge,要么rebase,



![img](https:////upload-images.jianshu.io/upload_images/225323-ea0266af1ba34a45.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/910/format/webp)

image

记住在这个场景中，你rebase到John/feature分支的操作并不违反rebase的黄金定律，因为：

只有你的local本地私有（还未push的） `feature commits`被移动和重写历史了，而你的本地commit之前的所有commit都未做改变。这就像是说“把我的改动放到John的工作之上”。在大多数情况下，这种rebase比用merge要好很多

上面这个例子中展示了已经在中央库存在的feature分支，两个开发人员做了对feature分支针对master做rebase操作后，再次push并且同步工作带来的灾难：历史混乱，并且merge后存在多个完全相同的changeset。
 结论：只要你的分支上需要rebase的所有commits历史还没有被push过(比如上例中rebase时从分叉处开始有两个commit历史会被重写)，就可以安全地使用git rebase来操作。

### 用rebase解决rebase

在本例中另一种简单的方法是使用 git pull --rebase 命令而不是直接 git pull。 又或者你可以自己手动完成这个过程，先 `git fetch`，再 `git rebase teamone/master`。

## 总结

现在，让我们回到之前的问题上来，到底合并还是变基好？希望你能明白，这并没有一个简单的答案。 Git 是一个非常强大的工具，它允许你对提交历史做许多事情，但每个团队、每个项目对此的需求并不相同。 既然你已经分别学习了两者的用法，相信你能够根据实际情况作出明智的选择。

总的原则是，只对尚未推送或分享给别人的**本地修改**执行变基操作清理历史，从不对已推送至别处的提交执行变基操作**，这样，你才能享受到两种方式带来的便利。
 一句话，私有的用rebase，公开的用merge。

用rebase：

- 一个人工作
- 私有分支
- 本地内容

用merge

- 多人协作
- 公开分支
- 公开内容

#### 总结2

搞清楚这个问题首先要搞清楚merge和rebase背后的含义。先看merge，官方文档给的说明是：git-merge - Join two or more development histories together顾名思义，当你想要两个分支交汇的时候应该使用merge。根据官方文档给的例子，是master merge topic，如图：



```undefined
                     A---B---C topic
                    /         \
               D---E---F---G---H master
```

然而在实践中，在H这个commit上的merge经常会出现merge conflict。为了避免解决冲突的时候引入一些不必要的问题，工程中一般都会规定no conflict merge。比如你在github上发pull request，如果有conflict就会禁止merge。所以才会有题主问的问题：在当前的topic分支，想要引入master分支的F、G commit上的内容以避免merge conflict，方便最终合并到master。这种情况下用merge当然是一个选项。用merge代表了topic分支与master分支交汇，并解决了所有合并冲突。然而merge的缺点是引入了一次不必要的history join。如图：



```undefined
       A--B--C-X topic
                    /       / \
               D---E---F---G---H master
```

其实仔细想一下就会发现，在引入master分支的F、G commit这个问题上，我们并没有要求两个分支必须进行交汇(join)，我们只是想避免最终的merge conflict而已。rebase是另一个选项。rebase的含义是改变当前分支branch out的位置。这个时候进行rebase其实意味着，将topic分支branch out的位置从E改为G，如图：



```undefined
                  A---B---C topic
                            /         
               D---E---F---G master
```

在这个过程中会解决引入F、G导致的冲突，同时没有多余的history join。但是rebase的缺点是，改变了当前分支branch out的节点。如果这个信息对你很重要的话，那么rebase应该不是你想要的。rebase过程中也会有多次解决同一个地方的冲突的问题，不过可以用squash之类的选项解决。个人并不认为这个是rebase的主要问题。综上，其实选用merge还是rebase取决于你到底是以什么意图来避免merge conflict。实践上个人还是偏爱rebase。一个是因为branch out节点不能改变的情况实在太少。另外就是频繁从master merge导致的冗余的history join会提高所有人的认知成本。