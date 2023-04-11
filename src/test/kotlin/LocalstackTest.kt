import io.kotest.core.spec.style.DescribeSpec
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

class LocalstackTest : DescribeSpec({
    describe("Localstack") {
        it("starts S3 & SQS with version 1.4") {
            startAndStopLocalstack("1.4")
        }

        // Works locally on macOS
        it("cannot start S3 & SQS with the latest version") {
            startAndStopLocalstack("latest")
        }
    }
})

private fun startAndStopLocalstack(version: String) {
    LocalStackContainer(DockerImageName.parse("localstack/localstack:$version")).apply {
        waitingFor(Wait.forLogMessage(".*set \\+x.*", 1))
        withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.SQS)
        withFileSystemBind(
            LocalstackTest::class.java.getResource("init-localstack.sh")!!.path,
            "/docker-entrypoint-initaws.d/init.sh",
            BindMode.READ_ONLY
        )
        start()
        stop()
    }
}
