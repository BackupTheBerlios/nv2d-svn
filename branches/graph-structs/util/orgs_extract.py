'''
This is a little datamining script that gathers coauthor frequency and time
data from the orgstudies database.  This script uses the the DBI api and so
only a few lines (import & connect) need to be altered to switch databases.
The data structures in this file are static.
'''
import MySQLdb
import string

_site = ""
_login = ""
_passw = ""

def enclose(s):
	return '"' + str(s) + '"'

def stringify(edges):
	rval = ''
	for x in edges:
		rval = rval + str(x) + ','

	return string.rstrip(rval, ',')

db = MySQLdb.connect(_site, _login, _passw)

cursor = db.cursor()

cursor.execute('use orgstudies')
cursor.execute('select author, fname, middle, last, coauthors_all, org, dept from authors_unique')

data = cursor.fetchall()


print '"Organization";"Department";"Last Published";"URL"'

for entry in data:
	# first and last names
	name = entry[3] + ', '
	if entry[1] != '':
		name = name + entry[1][0] + '.'
	if entry[2] != '':
		# add second initial if any
		name = name + entry[2][0] + '.'

	# go through coauthors
	lengths = []
	years = []
	coauthors = entry[4].split(';')
	for author2 in coauthors:
		#name = name.replace("'", "\\'")
		#author2 = author2.replace("'", "\\'")
		author2id = string.replace(author2, ',', '');
		author2id = string.replace(author2id, '.', '');
		author2id = string.upper(author2id);

		cursor.execute('select artl_title, artl_timep, authors FROM '\
		+ 'articles_sample WHERE '\
		+ 'locate("' + name + '", authors) > 0 and locate("' + author2 + '", authors) > 0 '\
		+ 'ORDER BY artl_timep DESC')
		
		artl_common = cursor.fetchall();
		if len(artl_common) < 1:
			print "Error for " + name + " and " + author2 + " pair -- no articles found"
			years.append(author2id+'=0')
		else:
			years.append(author2id + '=' + str(artl_common[0][1]))

		lengths.append(len(artl_common))

	edges = string.upper(entry[4])
	edges = string.replace(edges, ',', '')
	edges = string.replace(edges, '.', '')
	edges = string.replace(edges, ';', ',')

	#output = []
	#output.append(entry[0])		# name/id
	#output.append(edges)		# edges
	#output.append(lengths)		# tie lengths
	#output.append(entry[5])		# organization
	#output.append(entry[6])		# department
	#output.append(years)			# last published

	# so here is what we've got

	fullname = entry[1] + ' ' + entry[2] + ' ' + entry[3]

	buf = enclose(entry[0]) + ';' + enclose(edges) + ';' + enclose(stringify(lengths)) + ';'\
		+ enclose(fullname) + ';' + enclose(entry[5]) + ';' + enclose(entry[6]) + ';' + enclose(stringify(years)) + ';'\
		+ enclose("http://www.orgstudies.org/nv2d_show?last=" + entry[3])
	print buf
