
variable "size_instance" {
  type = string
  default = "t2.micro"
  description = "This is instance size"
}
variable "size_instance_kafka" {
  type = string
  default = "t2.small"
  description = "This is instance size kafka instance"
}
variable "ami_ubuntu" {
  type = string
  default = "ami-0df7a207adb9748c7"
  description = "This is ami for ubuntu instance"
}

variable "key-name" {
  type        = string
  default     = "ansible-hosts-v2.pem"
  description = "This is the key pair name for SSH access"
}