## Security setup

### Encrypting property file entries at command line

input = the string to be encrypted
password = Jasypt's password

#### Windows

```bat
java -cp \Users\xyz\.m2\repository\org\jasypt\jasypt\1.9.3\jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="xxx" password="yyy" ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator algorithm=PBEWITHHMACSHA512ANDAES_256
```

#### Unix variants

```shell
java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="xxx" password="yyy" ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator algorithm=PBEWITHHMACSHA512ANDAES_256
```