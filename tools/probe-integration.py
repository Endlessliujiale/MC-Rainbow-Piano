import zipfile
with zipfile.ZipFile('backup/before-user-piano/pianofx-0.1.0-score-staff-fixed.jar') as o,zipfile.ZipFile('dist/pianofx-0.1.0-extended-piano.jar') as n:
 a={x:o.read(x) for x in o.namelist() if not x.endswith('/')};b={x:n.read(x) for x in n.namelist() if not x.endswith('/')}
 print('CHANGED',*[x for x in a if a[x]!=b.get(x)],sep='\n')
 print('ADDED',*[x for x in b if x not in a],sep='\n')