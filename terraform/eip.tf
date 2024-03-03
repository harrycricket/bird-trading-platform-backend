# resource "aws_eip" "wolfalone-eip" {
#   instance = aws_instance.wolfalone.id
#   vpc      = true
# }

resource "aws_eip_association" "eip-association-to-bs2nd" {
  instance_id   = aws_instance.bs2nd.id
  allocation_id = "	eipalloc-0aa30bab31fb5b5c1"
}

resource "aws_eip_association" "eip-association-to-kafka" {
  instance_id   = aws_instance.kafka.id
  allocation_id = "eipalloc-09f5d301d6dc7e364"
}
