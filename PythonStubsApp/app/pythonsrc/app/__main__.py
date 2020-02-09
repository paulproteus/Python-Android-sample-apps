import sys
from . import Application, do_everything

app = Application()
print("app launched", app, file=sys.stderr)
do_everything()
