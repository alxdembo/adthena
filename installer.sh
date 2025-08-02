# install.sh
#!/bin/bash
set -e

echo "Installing PriceBasket..."

# Check we're running as root
if [[ $EUID -ne 0 ]]; then
   echo "Please run with sudo: sudo ./installer.sh"
   exit 1
fi

sbt assembly

# Install
mkdir -p /opt/price-basket
cp target/scala-*/price-basket.jar /opt/price-basket/

# Create command
cat > /usr/local/bin/PriceBasket << 'EOF'
#!/bin/bash
java -jar /opt/price-basket/price-basket.jar "$@"
EOF

chmod +x /usr/local/bin/PriceBasket

echo "✅ Installation complete!"
echo "Usage: PriceBasket Apples Milk Bread"