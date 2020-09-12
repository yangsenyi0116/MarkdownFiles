下面，我用自己的语言，试图写一篇比较好懂的 KMP 算法解释。

　　1.

![img](http://images.cnitblog.com/news/66372/201305/01202015-8a366b516a894594a30c3e3bd84c0a27.png)

　　首先，字符串"BBC ABCDAB ABCDABCDABDE"的第一个字符与搜索词"ABCDABD"的第一个字符，进行比较。因为B与A不匹配，所以搜索词后移一位。

　　2.

![img](http://images.cnitblog.com/news/66372/201305/01202018-018d6a1563164b43914f57259298c941.png)

　　因为B与A不匹配，搜索词再往后移。

　　3.

![img](http://images.cnitblog.com/news/66372/201305/01202014-9c017cfa23f2441bade3d6298af2f7cc.png)

　　就这样，直到字符串有一个字符，与搜索词的第一个字符相同为止。

　　4.

![img](http://images.cnitblog.com/news/66372/201305/01202015-b9dc0952fbb741b5ad2b5baccf201abf.png)

　　接着比较字符串和搜索词的下一个字符，还是相同。

　　5.

![img](http://images.cnitblog.com/news/66372/201305/01202016-d44c7f132b6f48b8890c7b286ccbba1c.png)

　　直到字符串有一个字符，与搜索词对应的字符不相同为止。

　　6.

![img](http://images.cnitblog.com/news/66372/201305/01202016-d63c25549b7a407aa85393da5edba6a9.png)

　　这时，最自然的反应是，将搜索词整个后移一位，再从头逐个比较。这样做虽然可行，但是效率很差，因为你要把"搜索位置"移到已经比较过的位置，重比一遍。

　　7.

![img](http://images.cnitblog.com/news/66372/201305/01202016-4ccbf851ae8146989adac22c2ba5e35b.png)

　　一个基本事实是，当空格与D不匹配时，你其实知道前面六个字符是"ABCDAB"。KMP 算法的想法是，设法利用这个已知信息，不要把"搜索位置"移回已经比较过的位置，继续把它向后移，这样就提高了效率。

　　8.

![img](http://images.cnitblog.com/news/66372/201305/01202017-55be40a6d2ba4e9093b5ddd2c69fff42.png)

　　怎么做到这一点呢？可以针对搜索词，算出一张《部分匹配表》（Partial Match Table）。这张表是如何产生的，后面再介绍，这里只要会用就可以了。

　　9.

![img](http://images.cnitblog.com/news/66372/201305/01202016-e5820f35e8d04f78ab4173cf077009a1.png)

　　已知空格与D不匹配时，前面六个字符"ABCDAB"是匹配的。查表可知，最后一个匹配字符B对应的"部分匹配值"为2，因此按照下面的公式算出向后移动的位数：

> 移动位数 = 已匹配的字符数 - 对应的部分匹配值

　　因为 6 - 2 等于4，所以将搜索词向后移动 4 位。

　　10.

![img](http://images.cnitblog.com/news/66372/201305/01202016-aebf6f1d0bc4459d96a8e464dfb197de.png)

　　因为空格与Ｃ不匹配，搜索词还要继续往后移。这时，已匹配的字符数为2（"AB"），对应的"部分匹配值"为0。所以，移动位数 = 2 - 0，结果为 2，于是将搜索词向后移 2 位。

　　11.

![img](http://images.cnitblog.com/news/66372/201305/01202017-6a37d41d5dda4486ba64fdb9a2f9f0e2.png)

　　因为空格与A不匹配，继续后移一位。

　　12.

![img](http://images.cnitblog.com/news/66372/201305/01202017-e87ea9e8a1d24c15ab8e67e0e07a40da.png)

　　逐位比较，直到发现C与D不匹配。于是，移动位数 = 6 - 2，继续将搜索词向后移动 4 位。

　　13.

![img](http://images.cnitblog.com/news/66372/201305/01202019-a4260d6b2aed47f7bb18b20e11dd1766.png)

　　逐位比较，直到搜索词的最后一位，发现完全匹配，于是搜索完成。如果还要继续搜索（即找出全部匹配），移动位数 = 7 - 0，再将搜索词向后移动 7 位，这里就不再重复了。

　　14.

![img](http://images.cnitblog.com/news/66372/201305/01202017-18d5ac5e91cf48179ff54a428af66590.png)

　　下面介绍《部分匹配表》是如何产生的。

　　首先，要了解两个概念："前缀"和"后缀"。 "前缀"指除了最后一个字符以外，一个字符串的全部头部组合；"后缀"指除了第一个字符以外，一个字符串的全部尾部组合。

　　15.

![img](http://images.cnitblog.com/news/66372/201305/01202017-ed903c8a7fc7447998aae6926fceefe6.png)

　　"部分匹配值"就是"前缀"和"后缀"的最长的共有元素的长度。以"ABCDABD"为例，

> －"A"的前缀和后缀都为空集，共有元素的长度为0；
>
> －"AB"的前缀为[A]，后缀为[B]，共有元素的长度为0；
>
> －"ABC"的前缀为[A, AB]，后缀为[BC, C]，共有元素的长度0；
>
> －"ABCD"的前缀为[A, AB, ABC]，后缀为[BCD, CD, D]，共有元素的长度为0；
>
> －"ABCDA"的前缀为[A, AB, ABC, ABCD]，后缀为[BCDA, CDA, DA, A]，共有元素为"A"，长度为1；
>
> －"ABCDAB"的前缀为[A, AB, ABC, ABCD, ABCDA]，后缀为[BCDAB, CDAB, DAB, AB, B]，共有元素为"AB"，长度为2；
>
> －"ABCDABD"的前缀为[A, AB, ABC, ABCD, ABCDA, ABCDAB]，后缀为[BCDABD, CDABD, DABD, ABD, BD, D]，共有元素的长度为0。

　　16.

![img](http://images.cnitblog.com/news/66372/201305/01202017-42813513df074cfa95f9f6a3b35adb42.png)

　　"部分匹配"的实质是，有时候，字符串头部和尾部会有重复。比如，"ABCDAB"之中有两个"AB"，那么它的"部分匹配值"就是2（"AB"的长度）。搜索词移动的时候，第一个"AB"向后移动 4 位（字符串长度-部分匹配值），就可以来到第二个"AB"的位置。