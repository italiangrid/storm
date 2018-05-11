$ curl https://omii006-vm03.cnaf.infn.it:8443/test.vo/test.txt \
    --cert $X509_USER_PROXY \
    --capath /etc/grid-security/certificates \
    --cacert /usr/share/igi-test-ca/test0.cert.pem \
    -H "Range: 0-1,3-4,8-10"