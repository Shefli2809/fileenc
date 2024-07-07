# fileenc
Integrates strong encryption and user authentication for secure and user-friendly file protection while ensuring any other user on the same system cannot decrypt files not encrypted by him. Supports all kind of files.


Encryption and Decryption
These modules handle file encryption and decryption operations using the AES algorithm.
Encryption:
When the user selects a file and clicks the "Encrypt File" button, the app prompts the user to select a file to encrypt.
The selected file is encrypted using AES encryption with a secret key associated with the current user.
The encrypted file is saved with the ".enc" extension and the original file is deleted.
Decryption:
When the user selects an encrypted file and clicks the "Decrypt File" button, the app prompts the user to select the encrypted file.
The selected encrypted file is decrypted using AES decryption with the corresponding secret key.
The decrypted file is saved without the ".enc" extension and the enc file is deleted.

Secret Key Generation
Secret keys are generated for each user during registration.
The secret key is a random 128-bit key encoded in Base64 format.
The generateRandomKey() method generates a random key using the SecureRandom class and encodes it in Base64.

![image](https://github.com/Shefli2809/fileenc/assets/90629203/318d16c9-b471-469d-b289-58f54e332b01)

![image](https://github.com/Shefli2809/fileenc/assets/90629203/1ad87c72-b275-4398-b97a-92d0fd54d77e)

![image](https://github.com/Shefli2809/fileenc/assets/90629203/e3aefd94-18eb-406d-bdc2-02615e7209cb)

![image](https://github.com/Shefli2809/fileenc/assets/90629203/9f630fa2-0947-47f6-91d9-16c057b5a648)

