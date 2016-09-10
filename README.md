#Shader
该工程主要用来测试着色器的5个子类。目前已经完成BitmapShader的初步测试，通过绘制圆形ImageView这种常用控件来完成BitmapShader应用。  
##RoundImageView
效果图如下  
[普通效果](https://github.com/Bottlezn/GradientView/blob/master/app/src/main/res/drawable/screenshot.png)
###功能  
- 圆形头像
- 圆角内容
- 灰色头像
- 内容压缩  

###属性表和代码
属性名|含义|设置
---|---|---
borderColor|边框颜色|"#000000"格式
borderWidth|边框宽度|接收dp格式
fillType|填充模式|true图像可能被压缩，为了多展示内容
corner|圆角大小|接收dp格式
drawStyle|绘制类型|0是圆形，1是圆角
isGray|是否灰色|true是灰色（先阶段不建议使用）

下面是属性代码
```
 <!--圆形头像属性-->
    <declare-styleable name="RoundImageView">
        <attr name="borderColor" format="color" />
        <attr name="borderWidth" format="dimension" />
        <!--fillType 控制填充模式，为true可能会压缩图像-->
        <attr name="fillType" format="boolean" />
        <attr name="corner" format="dimension" />
        <!--只能是0或1，0是圆形，1是圆角，不设置默认是圆形-->
        <attr name="drawStyle" format="integer" />
        <!--true的画，将图像调整为灰色，但是如果遇到ColorDrawable的内容，不知道会发生什么化学反应-->
        <attr name="isGray" format="boolean" />
    </declare-styleable>
```
