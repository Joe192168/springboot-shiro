# springboot-shiro 基于springboot jwt shiro 前后端分离框架

#启动

1、执行db下的sql脚本



#验证流程

1、获得token

http://localhost:8080/login

![image-20200121154353168](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20200121154353168.png)



2、通过得到token，验证权限是否正确

http://localhost:8080/sysUser/getList

![image-20200121154736593](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20200121154736593.png)

3、无权访问验证

http://localhost:8080/sysUser/getUserlist

![image-20200121155853945](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20200121155853945.png)



4、登陆超时，并获取新token

![image-20200121160013767](C:\Users\Lenovo\AppData\Roaming\Typora\typora-user-images\image-20200121160013767.png)