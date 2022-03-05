## 插件介绍
该插件通过向事件监听器注入代码实现用户行为的捕获，并且通过TraceId,ParentTraceId链接用户行为形成用户的操作链，可以通过树结构构建出图形化工具分析用户的操作流程。
### 页面级
Activity、Fragment、Dialog、PopupWindow在构建时在相应的时机插入代码初始化当前页面级别的事件代码，并通过向RootView插入初始链路节点信息。

|类 			|注入类				  |create  										 |曝光统计开始/恢复			|曝光暂停			|destroy
|--------|--------|
|Activity   |AppCompatDelegateImpl	|onCreate(savedInstanceState: Bundle)    	  	|onResume()				|onStop()			|onDestroy()
|Fragment   |Fragment				|onViewCreated(savedInstanceState: Bundle)    	|onResume()				|onStop()			|onDestroyView()
|Dialog     |AppCompatDelegateImpl	|onCreate(savedInstanceState: Bundle)    		|onResume()				|onStop()			|onDestroy()
|PopupWindow|调用类				  |调用类调用PopupWindow.showXXX    			       |无				|无			|调用类调用PopupWindow.dismiss()

### 控件级
在基础控件的事件监听器接口回调注入相应的事件代码，并通过View树查找相应事件链路并记录该节点。

| 接口  |方法|系统控件
|--------|--------|
|android.view.View$OnClickListener    |onClick(View view)|All View
|android.widget.CompoundButton$OnCheckedChangeListener    |onCheckedChanged(CompoundButton buttonView, boolean isChecked)|Switch、CheckBox、RadioButton、ToggleButton、Chip
|com.google.android.material.chip.ChipGroup$OnCheckedChangeListener    |onCheckedChanged(ChipGroup group, @IdRes int checkedId)|ChipGroup
|android.widget.RadioGroup$OnCheckedChangeListener|onCheckedChanged(RadioGroup group, @IdRes int checkedId)|RadioGroup
|android.widget.SeekBar$OnSeekBarChangeListener|onStopTrackingTouch(SeekBar seekBar)|SeekBar
|android.widget.RatingBar$OnRatingBarChangeListener|onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)|RatingBar

### 自定义View
自定义View事件通过注解(@BehaviorView)或者通过配置形式(TODO)进行事件代码的注入，但是有着一定的限制：
1. 不支持函数式事件
2. 接口回调方法必须包含View对象
3. 由于Kotlin版本低于1.6.x只支持source级别重复注解的限制，所以自定义View仅能标注一个接口回调

@BehaviorViev相关字段:

| 字段 | 含义 |
|--------|--------|
|   event     			|   事件名称
|   function     		|   方法名称
|   functionDesc     	|   方法方法签名
|   interfaceClz     	|   接口全限路径名
|   contentViewId     	|   内容ViewId（可选）

### 集成
根目录build.gradle：
```
buildscript { 
	repositories {
    	...
    	maven { url 'https://jitpack.io' }						ADD
    }
    dependencies {
    	...
    	classpath 'com.github.lyqiai:behaviorPlugin:0.0.5'		ADD
    }
}

allprojects {
    repositories {
    	...
        maven { url 'https://jitpack.io' }						ADD
    }
}
```
模块build.gradle：
```
plugins {
	...
    id 'behavior'												ADD
}

dependencies {
	...
    implementation 'com.github.lyqiai:behavior:0.0.1'			ADD
}

```
### 初始化
```
BehaviorManager.init(this).setBehaviorListener { event ->
	// TODO
}
```
### 事件对象字段

| 字段 | 含义 |
|--------|--------|
|    event    |    事件类型    |
|    data    |    事件数据    |
|    elementId    |    触发viewID    |
|    elementType    |    触发view类型    |
|    elementContent    |    触发view内容    |
|    context    |    上下文    |
|    systemLanguage    |    语言    |
|    systemVersion    |    版本    |
|    systemModel    |    型号    |
|    deviceBrand    |    厂商    |
|    time    |    时间    |
|    traceId    |    链路ID    |
|    parentTraceId    |    链路父ID    |

### 事件类型
| 事件类型 | 事件数据 |
|--------|--------|
|    page_create    |    Behavior(event=page_create, data=, elementId=, elementType=, elementContent=, context=com.river.behaviordemo.SecActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:48:31, traceId=ef735948-2929-4002-aec0-89d995a373ea, parentTraceId=128fe7a1-f47c-43ce-a032-7a1cc2d23474)
|    page_destroy    |    Behavior(event=page_destroy, data={"totalStay":1646506135544}, elementId=, elementType=, elementContent=, context=com.river.behaviordemo.SecActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:48:55, traceId=bf939f67-7b98-417e-90d2-09ef7b6a3a49, parentTraceId=ef735948-2929-4002-aec0-89d995a373ea)
|    fragment_create    |   Behavior(event=fragment_create, data={"tag": DIALOG_FRAGMENT_DEMO}, elementId=, elementType=, elementContent=, context=com.river.behaviordemo.DemoDialog, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:49:29, traceId=7d10b546-3098-4824-931b-a7bf8aff92b4, parentTraceId=1c2a54e8-4241-4962-a1e8-dbeb75000033)
|    fragment_destroy    |    Behavior(event=fragment_destroy, data={"totalStay":13874,"tag":DIALOG_FRAGMENT_DEMO}, elementId=, elementType=, elementContent=, context=com.river.behaviordemo.DemoDialog, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:49:43, traceId=93761258-f9bc-43c5-8342-24d1da9b173e, parentTraceId=7d10b546-3098-4824-931b-a7bf8aff92b4)
|    dialog_create    |    Behavior(event=dialog_create, data=, elementId=, elementType=, elementContent=, context=android.view.ContextThemeWrapper, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:49:59, traceId=e283b8d1-601f-4acc-9a97-d35bb435c0ba, parentTraceId=b3d5e431-6f05-4445-b05f-ae9fae9d4318)
|    dialog_destroy    |    Behavior(event=dialog_destroy, data={"totalStay":1646506211812}, elementId=, elementType=, elementContent=, context=android.view.ContextThemeWrapper, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:50:11, traceId=a3e2e483-7ef7-4654-acb5-729411446a99, parentTraceId=e283b8d1-601f-4acc-9a97-d35bb435c0ba)
|    popup_create    |    Behavior(event=popup_create, data=, elementId=, elementType=, elementContent=, context=, systemLanguage=zh, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:56:02, traceId=1bb04aaa-05c9-482b-af54-76ec172315cd, parentTraceId=7edfe351-5d8a-4e43-88dd-bfe0212015cc)
|    popup_destroy    |    Behavior(event=popup_destroy, data=, elementId=, elementType=, elementContent=, context=, systemLanguage=zh, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:56:07, traceId=1886e5b4-3635-4a98-b568-fce076313f1b, parentTraceId=1bb04aaa-05c9-482b-af54-76ec172315cd)
|    check    |    Behavior(event=check, data=true, elementId=checkBox, elementType=com.google.android.material.checkbox.MaterialCheckBox, elementContent=CheckBox, context=com.river.behaviordemo.MainActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:49:29, traceId=1c2a54e8-4241-4962-a1e8-dbeb75000033, parentTraceId=28688ca6-262c-433f-852a-0c07fd474a0f)
|    chip_check    |    Behavior(event=chip_check, data={"text": "behaviorDemo"}, elementId=chipGroup, elementType=com.google.android.material.chip.ChipGroup, elementContent=behaviorDemo, context=com.river.behaviordemo.MainActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:49:23, traceId=6b7de125-ade6-40e3-8c4f-04f7c9748a72, parentTraceId=28688ca6-262c-433f-852a-0c07fd474a0f)
|    click    |    Behavior(event=click, data=, elementId=actionBtn, elementType=com.google.android.material.button.MaterialButton, elementContent=btn, context=com.river.behaviordemo.MainActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:48:31, traceId=128fe7a1-f47c-43ce-a032-7a1cc2d23474, parentTraceId=28688ca6-262c-433f-852a-0c07fd474a0f)
|    dialog_click    |    Behavior(event=dialog_click, data=, elementId=, elementType=android/widget/Button, elementContent=ok, context=android.view.ContextThemeWrapper, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:49:25, traceId=3782e264-2e5b-4e42-a9d2-533565640ee9, parentTraceId=c8253d6f-51f0-4722-a772-af6e5351312f)
|    radio_check    |    Behavior(event=radio_check, data={"text": "RadioButton1"}, elementId=radio_group, elementType=android.widget.RadioGroup, elementContent=RadioButton1, context=com.river.behaviordemo.MainActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:51:25, traceId=29d127e2-5ae4-46fb-8f8b-0586dee041d2, parentTraceId=28688ca6-262c-433f-852a-0c07fd474a0f)
|    rating_bar_change    |    Behavior(event=rating_bar_change, data={"numStars": 5, "stepSize": 0.5, "rating": 1.5}, elementId=ratingBar, elementType=androidx.appcompat.widget.AppCompatRatingBar, elementContent=, context=com.river.behaviordemo.MainActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:53:38, traceId=01a4855c-0b56-48c8-ad6d-522a56f1138c, parentTraceId=dc952e64-5b90-4236-983a-c44a7b29bb11)
|    rv_child_click    |    Behavior(event=rv_child_click, data={"addressType":"1","areaName":"把控","cityId":"110001","cityName":"Bur Dubai","countryCode":"971","countryId":"2","defaultType":"2","detailAddress":"啦啦","id":"351270201322373161","name":"按摩","phoneNum":"19925364253","provinceId":"110000","provinceName":"Dubai","street":"把控"}, elementId=tv_delete, elementType=com.yunlu.middleeast.views.ImageTextView, elementContent=删除, context=com.yunlu.middleeast.ui.addressBook.ui.AddressBookActivity, systemLanguage=zh, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:55:08, traceId=4acb469f-30fb-40ed-a84e-ab11bfcc2c34, parentTraceId=8b875fbd-1052-4093-a2eb-0ef85ffe3a0e)
|    rv_item_click    |    Behavior(event=rv_item_click, data="阿联酋", elementId=, elementType=androidx.constraintlayout.widget.ConstraintLayout, elementContent=, context=com.yunlu.middleeast.ui.country.ui.SwitchCountryActivity, systemLanguage=zh, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:55:47, traceId=049b03df-e9b4-42ac-829c-23b6b7cd1897, parentTraceId=9482b4ed-7ac0-44f9-bade-ebd8e3feb147)
|    seek_bar_progress    |    Behavior(event=seek_bar_progress, data={"max":100, "min": 100, "progress": 23}, elementId=seekBar, elementType=androidx.appcompat.widget.AppCompatSeekBar, elementContent=, context=com.river.behaviordemo.MainActivity, systemLanguage=en, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:53:52, traceId=7a0361b0-1700-4050-a857-22c26df93662, parentTraceId=dc952e64-5b90-4236-983a-c44a7b29bb11)
|自定义View事件|Behavior(event=iev_title_click, data=, elementId=, elementType=, elementContent=, context=, systemLanguage=zh, systemVersion=11, systemModel=sdk_gphone_x86, deviceBrand=google, time=2022-03-05 18:56:07, traceId=1886e5b4-3635-4a98-b568-fce076313f1b, parentTraceId=1bb04aaa-05c9-482b-af54-76ec172315cd)
