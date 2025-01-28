class InvalidPasswordError(Exception):
    """Raised when the provided password is invalid"""
    def __init__(self, message="Invalid password provided"):
        self.message = message
        super().__init__(self.message)
