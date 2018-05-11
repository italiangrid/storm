$ curl -X MOVE https://omii006-vm03.cnaf.infn.it:8443/test.vo/test.txt \
--cert $X509_USER_PROXY \
--capath /etc/grid-security/certificates \
--cacert /usr/share/igi-test-ca/test0.cert.pem \
-H "Destination: https://omii006-vm03.cnaf.infn.it:8443/test.vo/test2.txt"