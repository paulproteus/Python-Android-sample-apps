import os
import sys
from rubicon.java import JavaClass
from . import Application, do_everything

app = Application()
print("app launched", app, file=sys.stderr)

activity_class_name = os.environ['ACTIVITY_CLASS_NAME']
activity_class = JavaClass(activity_class_name)
print(activity_class.exampleStaticMethod())
activity_class.setPythonApp(app)

print("Stored app in Java field")
do_everything()
