import zipfile,pathlib
with zipfile.ZipFile('tools/minecraft-intermediary.jar') as z:pathlib.Path('tools/Screen.class').write_bytes(z.read('net/minecraft/class_437.class'))