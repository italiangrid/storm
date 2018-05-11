> PUT /test.vo/test.txt HTTP/1.1
> User-Agent: libdavix/0.4.0 neon/0.0.29
> Keep-Alive: 
> Connection: TE, Keep-Alive
> TE: trailers
> Host: omii006-vm03.cnaf.infn.it:8443
> Content-Length: 12
> Expect: 100-continue
> 
< HTTP/1.1 100 Continue
< HTTP/1.1 201 Created
< X-Content-Type-Options: nosniff
< X-XSS-Protection: 1; mode=block
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Strict-Transport-Security: max-age=31536000 ; includeSubDomains
< X-Frame-Options: DENY
< Set-Cookie: JSESSIONID=abwhzs5fhrwyf3jn2fb9u1uf;Path=/;Secure
< Expires: Thu, 01 Jan 1970 00:00:00 GMT
< Date: Fri, 13 Mar 2015 13:22:22 GMT
< Accept-Ranges: bytes
< ETag: "/storage/test.vo/test.txt_323800060"
< Transfer-Encoding: chunked
<