#!/bin/sh

cd /home/ec2-user
rm -rf CSS
git clone https://github.com/ColorSchemeSystem/CSS.git
sed -i 's/mysql-password/your-password/g' CSS/conf/application.product.conf
cd /home/ec2-user/CSS
play clean compile stage
cd /home/ec2-user/CSS/target/universal/stage
bin/colorschemesystem -Dconfig.file=/home/ec2-user/CSS/conf/application.product.conf > /dev/null 2>&1 &