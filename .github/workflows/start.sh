#!/bin/bash
# Get the IP address of the container
IP=$(hostname -i)

# Generate Apache configuration
cat <<EOF > /etc/apache2/sites-available/000-default.conf
<VirtualHost *:80>
    ServerName cardvisor
    DocumentRoot /var/www/html
    
    ErrorLog ${APACHE_LOG_DIR}/error.log
    CustomLog ${APACHE_LOG_DIR}/access.log combined
    
    <Directory /var/www/html>
        Options -Indexes +FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>

    ProxyRequests Off
    ProxyPreserveHost On
    ProxyPass /api http://$IP:7777/
    ProxyPassReverse /api http://$IP:7777/

    # 요청 경로가 실제로 존재하지 않으면 index.html 반환
    <IfModule mod_rewrite.c>
        RewriteEngine On
        RewriteRule ^index\.html$ - [L]
        RewriteCond %{REQUEST_FILENAME} !-f
        RewriteCond %{REQUEST_FILENAME} !-d
        RewriteCond %{REQUEST_URI} !^/api
        RewriteCond %{REQUEST_URI} !\.(js|css|png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot|ico)$
        RewriteRule . /index.html [L]
    </IfModule>

</VirtualHost>
EOF

# Start Spring Boot application
nohup java -jar /app/*.jar > /app/app.log &

# Start Apache in the foreground
/usr/sbin/apache2ctl -D FOREGROUND
