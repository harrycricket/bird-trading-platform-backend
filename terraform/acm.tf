resource "aws_acm_certificate" "acm_bs2nd" {
  domain_name       = "thongtienthienphuot.shop"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}
