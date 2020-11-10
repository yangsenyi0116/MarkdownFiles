# 1 Android UI

## 1.1 UI

- 用户界面(``User Interface`)时系统和用户之间进行交互和信息交换的媒介，它实现信息的内部形式与人类可以接收形式之间的转换
- 软件设计可以分为两个部分：编码设计与UI设计

## 1.2 Android UI

- Android应用界面包含用户课查看与之交互的所有内容。Android提供丰富多样的预置UI组件，例如结构化布局对象和UI空间，你可以利用这些组件为你的应用构建图形界面。Andorid还提供其他界面模块，用于构建特殊界面，例如对话框、通知和菜单
- Android UI都是由布局和控件组成的

# 2 布局

布局(layout)可定义应用中的界面结构。布局中所有元素均使用View和View Group对象的层次结构进行构建。**View通常会中用户可查看并进行交互的内容**。然而，**ViewGroup是不可见容器，用于定义view和其他view group对象的布局机构**

## 2.1 布局的结构

![image-20201005000712540](C:/Users/vip87/AppData/Roaming/Typora/typora-user-images/image-20201005000712540.png)

- ·view·对象通常称为微件，可以是众多子类之一，例如`Button`或`TextView`
- `ViewGroup`对象通常称为布局，可以是提供其他布局结构的众多类型之一，例如`LinearLayout`或`ConstrainLayout`

## 2.2 声明布局

- 在Xml中声明元素，ANdroid提供对应View类及其子类的简明Xml词汇，如用于未见和布局的词汇。也可以使用Android Studio的Layout Editor，并财通拖放界面来构建XML布局

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- 在运行时实例化布局元素。你的应用可以编程创建View对象和ViewGroup对象（并操作其属性）

  ```java
  ConstraintLayout constrainLayout = new ConstraintLayout(this);
  TextView view = new TextView(this);
  view.setText("Hello World!");
  constrainLayout.addView(view);
  ```

  - 提示 : 使用 Layout Inspector 调试布局，可以查看通过代码创建的布局
  1. 在连接的设备或模拟器上[运行您的应用]。
  2. 依次点击 Tools > Layout Inspector。
  3. 在显示的 Choose Process 对话框中，选择要检查的应用进程，然后点击 OK。