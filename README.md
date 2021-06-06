## 组件化路由框架实现

--------
* 1. 自定义注解
元注解：
元注解是用来定义其他注解的注解(在自定义注解的时候，需要使用到元注解来定义我们的注解)。java.lang.annotation提供了四种元注解：@Retention、 @Target、@Inherited、@Documented。
| 元注解 | 说明 |
| :-----| :---- |
| @Target | 表明我们注解可以出现的地方。是一个ElementType枚举 |
| @Retention | 这个注解的的存活时间 |
| @Document | 表明注解可以被javadoc此类的工具文档化 |
| @Inherited | 是否允许子类继承该注解，默认为false |


| @Target ElementType类型 | 说明 |
| :-----| :---- |
| ElementType.TYPE | 接口、类、枚举、注解 |
| ElementType.FIELD  |  字段  |
| ElementType.METHOD |  方法  |
| ElementType.PARAMETER  | 方法参数 |
| ElementType.CONSTRUCTOR | 构造函数 |
| ElementType.LOCAL_VARIABLE | 局部变量 |
| ElementType.ANNOTATION_TYPE | 注解 |
| ElementType.PACKAGE | 包 |

| @Retention RetentionPolicy类型  | 说明 |
| :-----| :---- |
| RetentionPolicy.SOURCE | 注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃 |
| RetentionPolicy.CLASS  | 注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期 |
| RetentionPolicy.RUNTIME | 注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在 |


* 2.编写注解和注解处理器
创建java library Module这里命名为routeCompiler（必须为java library）

自定义注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Route {
    String value();
}
```

实现注解处理器
```java
@SupportedAnnotationTypes("com.liufuyi.routecompiler.Route")
@SupportedOptions("moduleName")
public class RouterProcessor extends AbstractProcessor {

  @Override
   public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
       .....
   }

}
```


* 3.注册注解处理器
在编写注解处理器的library下，创建一个resources目录,然后在该目录下创建META-INF目录，再在META-INF目录下创建services目录。
在services目录下面创建一个javax.annotation.processing.Processor的文本文件，内容就是自定义的注解处理器的全路径
我这里是com.liufuyi.routecompiler.RouterProcessor

* 4.使用注解处理器
当我们用户需要使用注解的时候，就需要在所使用的module的gradle下面添加

```java
annotationProcessor project(':routeCompiler')
```

* 5.调用生成好的类进行注册
在Route类里面，取名为init方法，用户只需要在application初始化的时候调用即可。
```java
class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
    }
}
```