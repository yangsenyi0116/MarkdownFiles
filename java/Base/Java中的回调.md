- Class A实现接口CallBack callback——**背景1**
- class A中包含一个class B的引用b ——**背景2**
- class B有一个参数为callback的方法f(CallBack callback) ——**背景3**
- A的对象a调用B的方法 f(CallBack callback) ——A类调用B类的某个方法 C
- 然后b就可以在f(CallBack callback)方法中调用A的方法 ——B类调用A类的某个方法D

```java
/**
 * 这是一个回调接口
 * @author xiaanming
 *
 */
public interface CallBack {
	/**
	 * 这个是小李知道答案时要调用的函数告诉小王，也就是回调函数
	 * @param result 是答案
	 */
	public void solve(String result);
}
```

```java
/**
 * 这个是小王
 * @author xiaanming
 * 实现了一个回调接口CallBack，相当于----->背景一
 */
public class Wang implements CallBack {
	/**
	 * 小李对象的引用
	 * 相当于----->背景二
	 */
	private Li li; 
 
	/**
	 * 小王的构造方法，持有小李的引用
	 * @param li
	 */
	public Wang(Li li){
		this.li = li;
	}
	
	/**
	 * 小王通过这个方法去问小李的问题
	 * @param question  就是小王要问的问题,1 + 1 = ?
	 */
	public void askQuestion(final String question){
		//这里用一个线程就是异步，
		new Thread(new Runnable() {
			@Override
			public void run() {
				/**
				 * 小王调用小李中的方法，在这里注册回调接口
				 * 这就相当于A类调用B的方法C
				 */
				li.executeMessage(Wang.this, question); 
			}
		}).start();
		
		//小网问完问题挂掉电话就去干其他的事情了，诳街去了
		play();
	}
 
	public void play(){
		System.out.println("我要逛街去了");
	}
 
	/**
	 * 小李知道答案后调用此方法告诉小王，就是所谓的小王的回调方法
	 */
	@Override
	public void solve(String result) {
		System.out.println("小李告诉小王的答案是--->" + result);
	}
	
}
```

```java
/**
 * 这个就是小李啦
 * @author xiaanming
 *
 */
public class Li {
	/**
	 * 相当于B类有参数为CallBack callBack的f()---->背景三
	 * @param callBack  
	 * @param question  小王问的问题
	 */
	public void executeMessage(CallBack callBack, String question){
		System.out.println("小王问的问题--->" + question);
		
		//模拟小李办自己的事情需要很长时间
		for(int i=0; i<10000;i++){
			
		}
		
		/**
		 * 小李办完自己的事情之后想到了答案是2
		 */
		String result = "答案是2";
		
		/**
		 * 于是就打电话告诉小王，调用小王中的方法
		 * 这就相当于B类反过来调用A的方法D
		 */
		callBack.solve(result); 
 
		
		
	}
	
}
```

