$ curl -X COPY https://omii006-vm03.cnaf.infn.it:9443/test.vo/test.txt \
--cert $X509_USER_PROXY \
--capath /etc/grid-security/certificates \
--cacert /usr/share/igi-test-ca/apostrofe.cert.pem \
-H "Destination: https://omii006-vm03.cnaf.infn.it:9443/test.vo/test3.txt"