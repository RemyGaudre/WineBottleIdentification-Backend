import ast
import cv2



def main():
    f = open("problem.txt", 'r').read()

    for x in list(ast.literal_eval(f)):
        print()
        print(x)
        image0 = cv2.imread(x[0])
        image1 = cv2.imread(x[1])
        cv2.imshow("image0", image0)
        cv2.moveWindow('image0', 100, 100)
        cv2.imshow("image1", image1)
        cv2.moveWindow('image1', 1000, 100)
        # waits for user to press any key
        # (this is necessary to avoid Python kernel form crashing)
        cv2.waitKey(0)
        # closing all open windows
        cv2.destroyAllWindows()

if __name__ == "__main__":
    main()