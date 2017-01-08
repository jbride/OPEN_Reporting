CERTS_HOME={{gpte_self_signed_certs_dir}}

rm -f $CERTS_HOME
mkdir -p $CERTS_HOME
fqdn=`hostname --fqdn`
log_file=/tmp/generate_certs.log
rm -f $log_file

echo -en "\n   generate unencrypted gpteserver.key  (by not passing -des3 flag) \n" >> $log_file
openssl genrsa -out $CERTS_HOME/gpteserver.key 4096 >> $log_file

# generate unencrpyted certificate aauthority root
openssl req -nodes \
            -new -x509 -days 3650 -key $CERTS_HOME/gpteserver.key \
            -out $CERTS_HOME/gpteserver.crt \
            -subj "/C=US/ST=NC/L=Raliegh/O=Red Hat/OU=GPTE/CN=$fqdn" >> $log_file

echo -en "\n inspect server certificate \n" >> $log_file
openssl x509 -pubkey -subject -issuer -email -dates -noout -in $CERTS_HOME/gpteserver.crt >> $log_file

echo -en "\n Verify server certificate \n" >> $log_file
openssl verify -verbose -CAfile $CERTS_HOME/gpteserver.crt $CERTS_HOME/gpteserver.crt >> $log_file


echo -en "\n generate unencrypted gpteclient.key (by not passing -des3 flag) \n" >> $log_file
openssl genrsa -out $CERTS_HOME/{{gpte_env}}_gpteclient.key 4096 >> $log_file

echo -en "\n create client cert signing request using client key (no need to specify a challenge password) \n" >> $log_file
openssl req -nodes \
        -new -key $CERTS_HOME/{{gpte_env}}_gpteclient.key \
        -out $CERTS_HOME/gpteclient.csr \
        -subj "/C=US/ST=NC/L=Raliegh/O=Red Hat/OU=GPTE/CN=$fqdn" >> $log_file

echo -en "\n create client cert by self-signing using server cert and key \n" >> $log_file
openssl x509 -req -days 365 -in $CERTS_HOME/gpteclient.csr \
                                -CA $CERTS_HOME/gpteserver.crt \
                                -CAkey $CERTS_HOME/gpteserver.key -set_serial 01 \
                                -out $CERTS_HOME/gpteclient.crt >> $log_file

echo -en "\n convert to PKCS format (so that it may be installed on most browsers) \n" >> $log_file
openssl pkcs12 -export -clcerts \
               -in $CERTS_HOME/gpteclient.crt \
               -inkey $CERTS_HOME/{{gpte_env}}_gpteclient.key \
               -out $CERTS_HOME/gpteclient.p12 \
               -passout pass: >> $log_file

echo -en "\n Combines client.crt and client.key into a single PEM file for programs using openssl. \n" >> $log_file
openssl pkcs12 -in $CERTS_HOME/gpteclient.p12 -out $CERTS_HOME/{{gpte_env}}_gpteclient.pem -clcerts -nokeys -passin pass: >> $log_file
