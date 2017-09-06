# Chips Explorer Installation Guide

This repository is based on the [Bitcoin Node Block Explorer](https://github.com/JornC/bitcoin-transaction-explorer) by JornC.

The installation guide assumes you're running a VPS with 2+ GB RAM, running Ubuntu 16.04, and using a user with sudoer permissions starting in the `/home/user` directory.

Optional steps are separated in blockquotes.

## 1. Install package dependencies needed for the installation process

```
sudo apt update
sudo apt install software-properties-common git python-software-properties unzip jq screen build-essential autoconf cmake libtool libprotobuf-c-dev libgmp-dev libsqlite3-dev python-dev libevent-dev pkg-config libssl-dev libcurl4-gnutls-dev
```

## 2. Install and configure Java 8

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt update
sudo apt install oracle-java8-installer
```

> Optionally, if other Java versions are running in the system you may want to configure the Java 8 version you just installed as default for both execution and compilation: 
>`update-alternatives --config java` and `update-alternatives --config javac`

Take note of your JAVA_HOME environment variable with `echo $JAVA_HOME`, since it will be needed later. In our example, this is `/usr/lib/jvm/java-8-oracle`.


## 3. Install Chips3 dependencies

```
sudo add-apt-repository ppa:bitcoin/bitcoin
sudo apt update
sudo apt install -y libdb4.8-dev libdb4.8++-dev
wget https://dl.bintray.com/boostorg/release/1.64.0/source/boost_1_64_0.zip
unzip boost_1_64_0.zip
cd boost_1_64_0
./bootstrap.sh
./b2
sudo ./b2 install
```


## 4. Install Chips3

```
git clone https://github.com/jl777/chips3.git
cd chips3
./autogen.sh
./configure --with-boost=/usr/local/
make
```

> to install the binaries in the system, run `cd src; sudo cp chipsd chips-cli /usr/local/bin`


## 5. Install Maven

This is required to build the explorer web app.
```
cd /opt
sudo wget http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
sudo tar -xvzf apache-maven-3.3.9-bin.tar.gz
sudo mv apache-maven-3.3.9 maven
```

Create a Maven environment configuration file:
```
sudo nano /etc/profile.d/mavenenv.sh
```

Add the following lines to the file:
```
export M2_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

Finally, load the configuration:
```
sudo chmod +x /etc/profile.d/mavenenv.sh
source /etc/profile.d/mavenenv.sh
```


## 6. Build the Chips Explorer app

```
cd; git clone https://github.com/SuperNETorg/chips-explorer.git
cd chips-explorer
mvn install
```


## 7. Install the Tomcat webserver

We'll use Tomcat to run the Java web app.
```
sudo groupadd tomcat
sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
cd /tmp
curl -O http://apache.mirrors.spacedump.net/tomcat/tomcat-8/v8.5.20/bin/apache-tomcat-8.5.20.tar.gz
sudo mkdir /opt/tomcat
sudo tar xzvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1
cd /opt/tomcat
sudo chgrp -R tomcat /opt/tomcat
sudo chmod -R g+r conf
sudo chmod g+x conf
sudo chown -R tomcat webapps/ work/ temp/ logs/
```

Make sure that the Tomcat service is using the correct JAVA_HOME path, by editing it in
```
sudo nano /etc/systemd/system/tomcat.service
```

In this file, you need to edit `Environment=JAVA_HOME=` so it matches your JAVA_HOME, appending `/jre` to it. In our example, this would be `Environment=JAVA_HOME=/usr/lib/jvm/java-8-oracle/jre`

> By default, Tomcat runs on port 8080. In order to make it run on the default https port, and since it is not recommended to run Tomcat as root (which would be needed to use a port below 1024 in Unix), we'll route port 80 to 8080 using iptables with `sudo iptables -t nat -A PREROUTING -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080`

Now we restart Tomcat with the configuration changes.
```
sudo systemctl daemon-reload
sudo systemctl start tomcat
```

> You can now check that tomcat is correctly running with `systemctl status tomcat`.

You should be able to find the Tomcat default page at `http://YOUR_IP`.
 
> If the default page does not show up, it might be due to Java binding to tcp6 addresses only. To fix tcp4/tcp6 binding issues you can force Tomcat Java to prefer tcp4 doing `sudo nano bin/setenv.sh` and adding the line `CATALINA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true "`.

Finally, enable tomcat at boot:
```
sudo systemctl enable tomcat
```


## 8. Configure the Tomcat manager

```
sudo nano /opt/tomcat/conf/tomcat-users.xml
```

Right before the last `</tomcat-users>` line, add `<user username="ADMIN_NAME" password="ADMIN_PASSWORD" roles="manager-gui,admin-gui"/>` replacing ADMIN_NAME and ADMIN_PASSWORD with the credentials you want to use to access the Tomcat manager.

For convenience, you may want to allow remote access to the Tomcat manager, by commenting out or removing the restriction to localhost in these two context files: 
```
sudo nano /opt/tomcat/webapps/manager/META-INF/context.xml
sudo nano /opt/tomcat/webapps/host-manager/META-INF/context.xml
```
Both files, ignoring comments, should look like this:
```
<?xml version="1.0" encoding="UTF-8"?>
<Context antiResourceLocking="false" privileged="true" >
</Context>
```


## 9. Run chipsd

Create a basic configuration file for chipsd:
```
cd; mkdir .chips
nano .chips/chips.conf
```
Add the following:
```
rpcport=57776
peerport=57777
rpcuser=chipsrpc
rpcpassword=your_secure_rpc_password
addnode=5.9.253.195
addnode=94.130.96.114
```
You can also include other options you need.

Start chipsd the way you prefer, for instance under a screen session:
```
screen -S chips
chipsd
```
Then hit Ctrl+A, then D to detach from the screen session. Chips will be now downloading the blockchain, you can check its status anytime using `chips-cli getinfo`.


## 10. Run the blockchain explorer app

Copy the Chips Explorer app built in step 6 to the Tomcat webapps directory:
```
cd; sudo cp chips-explorer/bitcoin-transactions-server/target/bitcoin-transactions-server-0.1.war /opt/tomcat/webapps/chips-explorer.war
```
Restart Tomcat.
```
sudo systemctl restart tomcat
```
Your Chips Blockchain Explorer will be now running at `http://YOUR_IP/chips-explorer`.


## 11. Configure the blockchain explorer

Finally, you need to configure some parameters in the explorer itself. In the search field enter `config` and press Enter. 
The different parameters are quite self explanatory, you just need to make sure that they match with the `chips.conf` contents you created in step 9.
