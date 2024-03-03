
# resource "aws_key_pair" "ansible" {
#   key_name   = "ansible-hosts-v2"
#   public_key = tls_private_key.rsa.public_key_openssh
# }
resource "tls_private_key" "rsa" {
  algorithm = "RSA"
  rsa_bits  = 4096
}
# resource "local_file" "tf-key" {
#   content  = tls_private_key.rsa.private_key_pem
#   filename = "ansible-hosts-v2"
# }

resource "aws_instance" "bs2nd" {
  ami             = var.ami_ubuntu
  instance_type   = var.size_instance
  key_name        = "ansible-hosts-v2"
  vpc_security_group_ids = ["${aws_security_group.allow_http.id}"]
  subnet_id       = aws_subnet.public-a.id
  tags = {
    Name = "bs2nd"
  }
  connection {
    type        = "ssh"
    user        = "ubuntu"
    host        = aws_instance.bs2nd.public_ip
    port        = 22
    private_key = tls_private_key.rsa.private_key_pem
  }
  user_data     = <<-EOF
    #!/bin/bash
    apt-get update -y
    apt-get install -y docker.io
    apt-get install -y awscli
    systemctl start docker
    systemctl enable docker
  EOF
}

resource "aws_instance" "kafka" {
  ami             = var.ami_ubuntu
  instance_type   = var.size_instance_kafka
  key_name        = "ansible-hosts-v2"
  vpc_security_group_ids = ["${aws_security_group.allow_http.id}"]
  subnet_id       = aws_subnet.public-a.id
  tags = {
    Name = "kafka"
  }
  connection {
    type        = "ssh"
    user        = "ubuntu"
    host        = aws_instance.kafka.public_ip
    port        = 22
    private_key = tls_private_key.rsa.private_key_pem
  }
  user_data     = <<-EOF
    #!/bin/bash
    apt-get update -y
    apt-get install -y docker.io
    apt-get install -y awscli
    apt-get install -y docker-compose
    systemctl start docker
    systemctl enable docker
  EOF
}
