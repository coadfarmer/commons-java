# common-utils

## 项目中直接使用，`pom.xml`中添加如下配置
``` xml
<project>
    ...
    <dependencies>
        <dependency>
            <groupId>com.cloume.common</groupId>
            <artifactId>commons-java</artifactId>
            <version>0.1.1-SNAPSHOT</version>
        </dependency>
        ...
    </dependencies>
    <repositories>
        <repository>
            <id>rdc-releases</id>
            <url>https://Qm6z0i:eqWmC4GH3c@repo.rdc.aliyun.com/repository/1635-release-VJVUjq/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>rdc-snapshots</id>
            <url>https://Qm6z0i:eqWmC4GH3c@repo.rdc.aliyun.com/repository/1635-snapshot-iAOTyg/</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
</project>
```

## ```class``` MapBuilder
快速的构建Map<K,V>实例
Easy to build a Map<K,V> instance.

## Usage

### create the builder
```
MapBuilder<String, String> builder = 
  MapBuilder.begin("hello", "world");
```
use method ```begin``` to create a ```MapBuilder``` instance, and indicate the generic type of the Map<```K,V```>, the arguments of method ```begin``` will be the first Entry in the Map

### add more entries

```
builder.and("good", "bye")
  .and("fine", "thank u")
  .and("ni", "hao");
```

### get the builded map

```
Map<String, String> map = builder.build();
```

### what about types beside ```String```
type of ```MapBuilder<K,V>``` is decided by the arguments' type of method ```begin```

```
MapBuilder<String, Integer> builder = 
  MapBuilder.begin("hello", 100);

MapBuilder<String, Object> builder = 
  MapBuilder.begin("a-key", (Object) "haha"));
```

### why u say **Easy**

somewhere u need a map
```
MapBuilder.begin("example", (Object) "1")
  .and("num", 100)
  .and("disabled", false)
  .and("params", MapBuilder.begin("param1", (Object) "nothing")
                  .and("param2", new int[]{0, 1, 2})
                  .and("bool", true)
                  .and("counter", new Integer(5))
                  .build())
  .and("ending", true)
  .build();
```
u can get a ```Map``` like:
```
{
  "example": "1",
  "num": 100,
  "disabled": false,
  "params": {
    "param1": "nothing",
    "param2": [0, 1, 2],
    "bool": true,
    "counter": 5
  },
  "ending": true
}
``` 

try it!

# commons-beanutils

[![TEST](https://img.shields.io/travis/HQIT/commons-beanutils.svg "travis-ci")](http://travis-ci.org/HQIT/commons-beanutils)

## usage
see tests

* construct

use static method ```com.cloume.commons.beanutils.Updater.wrap(R)``` create new instance, *Updater.wrap(```theObjectYouWantToUpdate```)* return the Updater<T> instance

```
class UpdatableClass { ... }
Updater<UpdatableClass> updater = Updater.wrap(new UpdatableClass());
```

* keys mapping

method ```com.cloume.commons.beanutils.Updater.mapping(String, String)``` use to map a key name to another, for example a class has a field named ```someField```, when there is a key named ```anotherField```, can use the ```mapping``` set the value of ```anotherField``` to the ```someField``` of target class instance. and method ```mapping``` return the Updater<T> instance self, so can write multi-mapping in chain

```
Updater<UpdatableClass> updater = updater
	.mapping("anotherField", "someField")
	.mapping("helloField", "hiField"); 
```

* update

there are two overwrited methods ```com.cloume.commons.beanutils.Updater.update(Map<String, Object>)``` and ```com.cloume.commons.beanutils.Updater.update(Map<String, Object>, IConverter)```, use to update the target instance from a Map<String, Object>.

1. update without converter

example:
```
class UpdatableClass {
	String someField;
	int hiField;
}

Map<String, Object> from = new HashMap<String, Object>();
from.put("someField", "hello,world");
from.put("helloField", 100);	///see mapping
UpdatableClass updated = updater.update(from);
```

1. update with converter

converter ```com.cloume.commons.beanutils.IConverter``` have two methods ```com.cloume.commons.beanutils.IConverter.convert(String, Object)``` must be implemented, and ```com.cloume.commons.beanutils.IConverter.pair(String, Object)``` provide an easy way create new ```Entry<K,V>``` instance. 
when using converter you can both maping key and modify the value to fit the class field needs.

example:
```
ObjectToUpdate updated = Updater.wrap(new ObjectToUpdate()).mapping("longValue2", "longValue").update(from,
	/// java8 can use lambda
	new IConverter() {
		@SuppressWarnings("unchecked")
		@Override
		public Entry<String, Object> convert(String key, Object value) {
			switch (key) {
			case "intValue": {
				return pair(key, Integer.parseInt(String.valueOf(value)));
			}
			case "arrayValue": {
				return pair(key, StringUtils.split(String.valueOf(value), ','));
			}
			case "stringValue2": {
				return pair("stringValue", value);
			}
			case "classValue": {
				return pair(key, Updater.wrap(new InnerClass()).update((Map<String, Object>) value));
			}
			}
			return pair(key, value);		///pass through
		}
	});
```

* complex field

example:
```
class InnerClass { String innerField; }
class UpdatableClass {
	String someField;
	int hiField;
	InnerClass classField;
}

Map<String, Object> from = new HashMap<String, Object>();
from.put("classField.innerField", "hello,world");
UpdatableClass updated = updater.update(from);
```

# http request body verifier

## 目标场景

RESTful api如果采用JSON承载数据的话, 经常性的POST这样的数据: 
```
{
  "foo": "hello,world",
  "foo3": 100,
  "foo8": { "question": "who am i?" }
}
```
为了安全，后端也需要对提交的数据进行有效性验证。

## 先来个例子
```
IResultCallback callback = ...;
Map<String, Object> target = ...;

boolean result = new Verifier()
  .result(callback)   //这是可选的
  //rule方法有5个重载
  .rule("foo")   //必须存在foo属性
  .rule("foo2", "[0-9]{1,3}") //foo2属性必须存在, 并且值必须是1到3位的数字字符串
  .rule("foo3", true, "[0-9]{1,3}") //foo3属性可以不存在, 如果有的话, 其值必须是1到3位的数字字符串
  .rule("foo4", new IChecker() {
    public boolean check(Object value) { ... }
  })    //通过实现IChecker对foo4值进行检查
  .rule("foo5", true, new IChecker() {
    public boolean check(Object value) { ... }
  })    //通过实现IChecker对foo5值进行检查, foo5可以不存在
  .verify(target);
```

上述例子是一般用法, 如果设置了```IResultCallback```的话, 除了返回结果```result```以外, ```IResultCallback```的方法```void result(boolean success, String reason);```中, 也可以获得校验结果和原因(第二个参数```reason```). 如果是java8+可以用lambda很方便的进行调用

## 检查器 IChecker

目前已经实现的检查器有3个:

* AlwaysTrueChecker
* AlwaysFalseChecker
* RegexChecker 通过正则表达式进行检查

如果这些检查器不够用, 可以像例子中的foo4/foo5一样自己实现新的检查器