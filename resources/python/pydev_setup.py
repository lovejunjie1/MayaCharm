import sys


def setup_pydevd():
    pydev_path = r"%1$s"
    if not pydev_path in sys.path:
        sys.path.append(pydev_path)


if __name__ == "__main__":
    setup_pydevd()
