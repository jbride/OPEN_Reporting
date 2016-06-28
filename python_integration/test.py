import mysql.connector

connection = mysql.connector.connect(
                                    host='localhost',
                                    user='root',
                                    password='',
                                    database='lms_transactional')

print("CONNECTED!")
cursor = connection.cursor()
query = ("""SELECT StudentAccreditations.AccreditationDate, Students.Email, Courses.CourseID
         FROM StudentAccreditations
         INNER JOIN Students
         ON StudentAccreditations.StudentID=Students.StudentID
         INNER JOIN Courses
         ON Courses.CourseID=StudentAccreditations.CourseID""")

cursor.execute(query)

def results():
    # if cursor:
    #     print(cursor{})
    for i in cursor:
        print(i)


print("Cursor: {}".format(cursor))
results()

# cursor.close()
connection.close()
