
ca私钥文件密码: changeitCa
ca_server.crt 是ca签发的SAN证书， CA是10.94.22.74、SAN是10.94.22.74、*.burukeyou.com、www.bkt01.com
selft_server.crt 是 ca_server.key 自己的私钥签发的证书

server.crt 是 server.key 自己签发的证书
server02.p12 密钥库包含 server.key 和 server.crt


服务端提供: ca_server.crt
客户端可以导入信任证书:  ca_server.crt、和 ca.crt，
    - ca.crt也可以是因为ca_server.crt是由ca签发的。
    - 证书链就是  ca_server.crt --》 ca.crt
    - 然后发现ca.crt在本地信任库是可信的， 然后就取出ca.crt的公钥A， 然后再用公钥A去验证ca_server.crt， 发现可以解签，所以也信任


 服务端提供: selft_server.crt
 客户端能导入信任证书:    selft_server.crt.
 -  ca_server.crt 和 ca.crt 的公钥证书都不可以。 因为该证书是server.key私钥自己签发的， 用ca的公钥肯定解不了，只能用自己公钥
