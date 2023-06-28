# resource "aws_eip" "wolfalone-eip" {
#   instance = aws_instance.wolfalone.id
#   vpc      = true
# }

resource "aws_eip_association" "eip-association-to-bs2nd" {
  instance_id   = aws_instance.bs2nd.id
  allocation_id = "eipalloc-0a25f2fa27a7221f5"
}


resource "aws_eip_association" "eip-association-to-kafka" {
  instance_id   = aws_instance.kafka.id
  allocation_id = "eipalloc-0cece09c6186ffd18"
}