# ethGateway
This is the final project of the course CS656 Computer Network of the University of Waterloo in 2017. Switching to git and uploaded to GitHub in 2018.

The ethereum contract is served as a database that maps queries to values. The Java program serves as a local DNS server which will intercept DNS requests and query the blockchain when necessary. (This allows the original DNS queries to perform normally.)

## Abstract
Domain Name Service helps translating IP and domain name. Despite of its wide adoption, the original design has several security vulnerabilities and problem of centralization. Recently, several blockchain based DNS was designed to solve centralization. However, not much is discussed of its efficiency and burden laid on the network. In this paper, a Domain Name Service was built on top of the Ethereum blockchain using smart contracts and deployed on Ropsten testnet. A prototype of local dns server were also provided to identify specific blockchain related DNS requests and resolve it locally by communicate with the blockchain. Lastly, We perform some tests and compare blockchain-based DNS with traditional DNS.
