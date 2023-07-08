
resource "aws_lb_target_group" "my-tg" {
  name     = "tf-lb-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.my-vpc.id
}




resource "aws_lb_target_group_attachment" "attach-game-cd-1" {
  target_group_arn = aws_lb_target_group.my-tg.arn
  target_id        = aws_instance.bs2nd.id
  port             = 80
}

resource "aws_lb_target_group_attachment" "attach-game-cd-2" {
  target_group_arn = aws_lb_target_group.my-tg.arn
  target_id        = aws_instance.bs2nd_2.id
  port             = 80
}

resource "aws_lb" "bs2nd" {
  name               = "bs2nd"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.allow_http.id]
  subnets            = [aws_subnet.public-a.id, aws_subnet.public-b.id]

  tags = {
    Name = "bs2nd"
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.bs2nd.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_listener_rule" "back-end" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my-tg.arn
  }

  condition {
    path_pattern {
      values = ["/*"]
    }
  }
  # condition {
  #   host_header {
  #     values = ["example.com"]
  #   }
  # }
}
# Create a listener for the ALB
resource "aws_lb_listener" "my-listener" {
  load_balancer_arn = aws_lb.bs2nd.arn
  port              = "443"
  protocol          = "HTTPS"
  certificate_arn   = aws_acm_certificate.acm_bs2nd.arn
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my-tg.arn
  }
}

resource "aws_lb_listener_rule" "back-end-v2" {
  listener_arn = aws_lb_listener.my-listener.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my-tg.arn
  }

  condition {
    path_pattern {
      values = ["/*"]
    }
  }
  # condition {
  #   host_header {
  #     values = ["example.com"]
  #   }
  # }
}