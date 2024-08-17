import pytest

def pytest_addoption(parser):
	parser.addoption(
		"--nc", default=False, action="store_true", help="don't recompile java code before running tests"
	)	
	parser.addoption(
		"--nocompile", default=False, action="store_true", help="don't recompile java code before running tests"
	)	

@pytest.fixture
def nocompile(request):
	'''
	Return True if compiling java files should be skipped (--nocompile or --nc is set)
	'''
	return request.config.getoption("--nc") or request.config.getoption("--nocompile")