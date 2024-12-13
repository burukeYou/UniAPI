
ca私钥文件密码: changeitCa
ca_server.crt 是ca.key对ca_server.csr签发的SAN证书， CA是10.94.22.74、SAN是10.94.22.74、*.burukeyou.com、www.bkt01.com
ca_server_no_san.crt,  是ca.key对ca_server.csr签发的SAN证书， 不带san

selft_server.crt 是 ca_server.key 自己的私钥签发的证书
server.crt 是 server.key 自己签发的证书
server02.p12 密钥库包含 server.key 和 server.crt, 密码： changeit


ca_client.crt 是 ca_client.key 自己签发自己的证书, CN是 www.client01.com
ca_client.pkcs12 密钥库包含 ca_client.crt、ca_client.key 密码： 123abc
ca_client_has_trust.pkcs12 比ca_client.pkcs12 多 import 进了 ca_client.crt 作为 trust条目, 密码一样


ca_client_has_trust.pkcs12  密钥库包含 ca_server.crt、ca_server.key 密码： 123abcd

# ==========================================================================================
服务端提供: ca_server.crt
客户端可以导入信任证书:  ca_server.crt、和 ca.crt，
    - ca.crt也可以是因为ca_server.crt是由ca签发的。
    - 证书链就是  ca_server.crt --》 ca.crt
    - 然后发现ca.crt在本地信任库是可信的， 然后就取出ca.crt的公钥A， 然后再用公钥A去验证ca_server.crt， 发现可以解签，所以也信任


 服务端提供: selft_server.crt
 客户端能导入信任证书:    selft_server.crt.
 -  ca_server.crt 和 ca.crt 的公钥证书都不可以
    如果用ca.crt：   抛出无法校验证书 unable to find valid certification path to requested target， 因为证书都没server.key的公钥
    如果用ca_server.crt： 虽然证书里有 server.key的公钥, 但是抛出 ValidatorException, is not a CA certificate
    如果用 ca_server_no_san.crt    抛出  SignatureException: Signature does not match.

