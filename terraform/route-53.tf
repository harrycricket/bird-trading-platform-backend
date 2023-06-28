# resource "aws_route53_record" "cname_route53_record" {
#   zone_id = "Z03649151L8H2LM79XFFC" # Replace with your zone ID
#   name    = "game-cd.1wolfalone1.com" # Replace with your subdomain, Note: not valid with "apex" domains, e.g. example.com
#   type    = "CNAME"
#   ttl     = "60"
#   records = [aws_lb.game_cd.dns_name]
# }

# See the Terraform Route53 Record docs

# You can add a basic CNAME entry with the following:

# resource "aws_route53_record" "cname_route53_record" {
#   zone_id = aws_route53_zone.primary.zone_id # Replace with your zone ID
#   name    = "www.example.com" # Replace with your subdomain, Note: not valid with "apex" domains, e.g. example.com
#   type    = "CNAME"
#   ttl     = "60"
#   records = [aws_lb.MYALB.dns_name]
# }
# Or if you're are using an "apex" domain (e.g. example.com) consider using an Alias (AWS Alias Docs):

resource "aws_route53_record" "alias_route53_record" {
  zone_id = "Z0136857TBRTIBYW528W" # Replace with your zone ID
  name    = "thongtienthienphuot.shop" # Replace with your name/domain/subdomain
  type    = "A"

  alias {
    name                   = aws_lb.bs2nd.dns_name
    zone_id                = aws_lb.bs2nd.zone_id
    evaluate_target_health = true
  }
}