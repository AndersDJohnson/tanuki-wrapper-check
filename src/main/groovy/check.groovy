class G {
	static dir = 'C:/dev/svn/root'
	static r1 = ~/(?i)^\s*wrapper\.java\.additional\.\d+=(.*\r?)/
	static r2 = ~/(?i)^\s*wrapper\.java\.additional\.\d+=.*(verbose\:gc|PrintGC).*\r?/
}

def scan() {
	def ant = new AntBuilder()
	def scanner = ant.fileScanner {
	    fileset(dir: G.dir) {
	        include(name: '**/*wrapper*conf*')
	    }
	}

	for (File f in scanner) {
	    handleFile(f)
	}
}

def handleFile(File f) {
	def text = f.text
	String stripped = stripVerbose(text)
	String reindexed = reindex(stripped)
	if (text != reindexed) {
		println "==FILE DIFF== $f"
//		println '==DIFF===================================='
//		println '===before===================================='
//		println text
//		println '===after===================================='
//		println reindexed
//		println '==DIFF===================================='
	}
    else {
        println "==FILE== $f"
    }
	f.text = reindexed
}

def stripVerbose(String text) {
	def lines = text.split('\\n', -1)
	def nlines = []
	for (line in lines) {
		if (line ==~ G.r2) {
//			println "match: $line"
		}
		else {
			nlines.add(line)
		}
	}

	def ntext = nlines.join('\n')
	return ntext
}


def reindex(String text) {
	def lines = text.split('\\n', -1)
	def nlines = []
	def i = 1
	for (line in lines) {
		def m = line =~ G.r1
		if (m) {
			def nline = 'wrapper.java.additional.' + i + '=' + m.group(1)
			nlines.add(nline)
			i++
		}
		else {
			nlines.add(line)
		}
	}

	def ntext = nlines.join('\n')
	return ntext
}


//handleFile(new File('C:/dev/svn/root/Enterprise/ServerConfig/CatalogServices_ServerConfig/AppServer/Tomcat/Environment/test/catalog-services/conf/wrapper.conf'))
//handleFile(new File('C:/dev/svn/root/Enterprise/Catalog/CatalogWeb/branches/2.6.0.PRD/ServerConfig/Tomcat/common/catalog/conf/wrapper-license.conf'))
scan()

println("OK")
