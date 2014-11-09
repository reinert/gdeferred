<!--
  Copyright 2013 Ray Tsang
  Copyright 2014 Danilo Reinert

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

GDeferred
=========
GDeferred is a fork of [JDeferred](https://github.com/jdeferred/jdeferred.git) - "Java Deferred/Promise library similar to JQuery's Deferred Object" - reimplemented for GWT.

<a name="features"></a>Features
--------
* Deferred Object and Promise
* Promise callbacks
  * ```.then(…)```
  * ```.done(…)```
  * ```.fail(…)```
  * ```.progress(…)```
  * ```.always(…)```
* Multiple promises ([soon!](https://github.com/reinert/gdeferred/issues/1))
  * ```.when(p1, p2, p3, …).then(…)```
* Java Generics support
  * ```Deferred<Integer, Exception, Double> deferred;```
  * ```deferred.resolve(10);```
  * ```deferred.reject(new Exception());```
  * ```deferred.progress(0.80);```
* Java 8 Lambda friendly

<a name="examples"></a>Quick Examples
==============

<a name="examples-deferred-promise"></a>Deferred Object and Promise
---------------------------

```java
Deferred deferred = new DeferredObject();
Promise promise = deferred.promise();
promise.done(new DoneCallback() {
  public void onDone(Object result) {
    ...
  }
}).fail(new FailCallback() {
  public void onFail(Object rejection) {
    ...
  }
}).progress(new ProgressCallback() {
  public void onProgress(Object progress) {
    ...
  }
}).always(new AlwaysCallback() {
  public void onAlways(State state, Object result, Object rejection) {
    ...
  }
});
```
With the reference to deferred object, you can then trigger actions/updates:

```java
deferred.resolve("done");
deferred.reject("oops");
deferred.progress("100%");
```

<a name="example-filter"></a>Filter
-----------
Filtering allows one to return a different promise of the *same* state.

```java
Deferred d = …;
Promise p = d.promise();
Promise filtered = p.then(new DoneFilter<Integer, Integer>() {
  public Integer filterDone(Integer result)
    return result * 10;
  }
});

filtered.done(new DoneCallback<Integer>{
  public void onDone(Integer result) {
    // result would be original * 10
    System.out.println(result);
  }
});

d.resolve(3) -> 30.
```

<a name="example-pipe"></a>Pipe
----
Piping allows one to return a different promise of the *any* state.

```java
Deferred d = ...;
Promise p = d.promise();

p.then(new DonePipe<Integer, Integer, Exception, Void>() {
  public Deferred<Integer, Exception, Void> pipeDone(Integer result) {
    if (result < 100) {
      return new DeferredObject<Integer, Void, Void>().resolve(result);
    } else {
      return new DeferredObject<Integer, Void, Void>().reject(new Exception(...));
    }
  }
}).done(...).fail(...);

d.resolve(80) -> done!
d.resolve(100) -> fail!
```

<a name="example-lambda"></a>Java 8 Lambda
-------------
Now this is pretty cool when used with Java 8 Lambda!

```Java
dm.when(() -> {
  return "Hey!";
}).done(r -> System.out.println(r));

dm.when(
  () -> { return "Hello"; },
  () -> { return "World"; }
).done(rs ->
  rs.forEach(r -> System.out.println(r.getResult()))
);
```

## Documentation
* [Javadocs](http://reinert.github.io/gdeferred/javadoc/apidocs/index.html)

## Downloads
GDeferred is currently available at maven central.

### Maven
```
<dependency>
    <groupId>io.reinert</groupId>
    <artifactId>gdeferred</artifactId>
    <version>0.9.0</version>
</dependency>
```

## License
GDeferred is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)

<!-- Google Code for GitHub Visit Conversion Page -->
<script type="text/javascript">
/* <![CDATA[ */
var google_conversion_id = 974052972;
var google_conversion_language = "en";
var google_conversion_format = "3";
var google_conversion_color = "ffffff";
var google_conversion_label = "wsVZCOycvgkQ7Ly70AM";
var google_conversion_value = 0;
var google_remarketing_only = false;
/* ]]> */
</script>
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/974052972/?value=0&amp;label=wsVZCOycvgkQ7Ly70AM&amp;guid=ON&amp;script=0"/>
</div>
</noscript>
